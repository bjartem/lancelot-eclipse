import re
from java.io import ByteArrayInputStream, BufferedInputStream, IOException
from java.lang import String
import zip_utils
from japa.parser import JavaParser
from japa.parser.ast.visitor import GenericVisitorAdapter
from japa.parser.ast.body import ClassOrInterfaceDeclaration, MethodDeclaration
from galahadjava import Tracker


_package_re = re.compile(r'^\s*package\s+([^;\s]+)\s*;\s*$', re.M)

class ThinClassAnalysisReport:
    def __init__(self, package_name, class_name, method_bug_reports):
        self.package_name = package_name.replace('/', '.')
        self.class_name = class_name
        self.method_bug_reports = method_bug_reports


class ThinMethodBugReport:
    def __init__(self, method_name, source_code,
                 best_matching_phrase, rules, violations):
        self.method_name = method_name
        self.source_code = source_code
        self.best_matching_phrase = best_matching_phrase
        self.rules = rules
        self.violations = violations


class SourceLocator:
    def __init__(self, zip_path):
        self._package_map = SourceLocator.build_package_map(zip_path)

    def process_class_report(self, class_report, method_locs):
        package_name = class_report.packageName
        class_name = class_report.className

        process_mbr = lambda mbr: self._process_method_bug_report(mbr,
                                         package_name, class_name, method_locs)
        return ThinClassAnalysisReport(
            package_name,
            class_name,
            map(process_mbr, class_report.methodBugReports)
        )

    def _process_method_bug_report(self, report, package_name,
                                   class_name, method_locs):
        location_key = (package_name,
                        class_name,
                        report.method.methodName, 
                        report.method.descriptor,)
        method_pos = method_locs[location_key]
        dot_package_name = package_name.replace('/', '.')

        source = ''
        if dot_package_name in self._package_map:
            package_source_locator = self._package_map[dot_package_name]
            source = package_source_locator.find_source(class_name, method_pos)
        else:
            print 'problem c for %s...' % class_name

        return ThinMethodBugReport(report.method.methodName, source,
                                   report.lookupResult.bestMatchingPhrase,
                                   report.lookupResult.rules,
                                   report.lookupResult.violations)

    @staticmethod
    def build_package_map(zip_path):
        res = {}
        for name, read_fn in zip_utils.get_entries(zip_path):
            if name.endswith('.java'):
                source_bytes = read_fn()
                source = str(String(source_bytes))
                package_name = SourceLocator.read_package_name(source)
                if package_name is None:
                    print 'log problem b'
                else:
                    print 'Adding source file for package "%s"...'%package_name
                    psl = res.setdefault(package_name, _PackageSourceLocator())
                    psl._source_bytes.append(source_bytes)
        return res

    @staticmethod
    def read_package_name(source):
        m = _package_re.search(source)
        return m.group(1) if m else None


class _PackageSourceLocator:
    def __init__(self):
        self._source_bytes = []
        self._location_map = None  

    def find_source(self, class_name, first_ins_line_num):
        self._find_locations()
        if not class_name in self._location_map:
            print 'Could not find source for "%s"...' % class_name
            return ''
        candidates = self._location_map[class_name]
        best_dist, source = 1E7, ''
        for cand in candidates:
            dist = first_ins_line_num - cand.beginLine
            if dist >= 0 and dist < best_dist:
                best_dist = dist
                source = cand.source
        return source
    
    def _find_locations(self):
        if not self._location_map is None:
            return

        tracker = Tracker()
        for sb in self._source_bytes:
            cu = JavaParser.parse(ByteArrayInputStream(sb))
            cu.accept(tracker, None)

        self._location_map = {}
        for loc in tracker.trackedMethods:
            self._location_map.setdefault(loc.qualifiedName, []).append(loc)
