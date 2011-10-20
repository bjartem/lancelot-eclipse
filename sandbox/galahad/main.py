from __future__ import with_statement

import re
import itertools
import jarray
import java.io
from java.io import BufferedInputStream, ByteArrayInputStream
import java.net
from java.util.zip import ZipFile
from no.nr.lancelot.frontend import LancelotRegistry,\
                                    ClassAnalysisOperation,\
                                    MethodLocationFinder
from html_renderer import HTMLReportRenderer
from japa.parser import JavaParser
import japa.parser.ast.visitor
from japa.parser.ast.visitor import GenericVisitorAdapter
from japa.parser.ast.body import TypeDeclaration, MethodDeclaration
from locator import SourceLocator
import zip_utils

class LancelotClassAnalyser:
    @staticmethod
    def ensure_lancelot_initialized():
        if not LancelotRegistry.isInitialized():
            LancelotRegistry.initialize(
                java.net.URL('file:///home/edvard/dev/nr/lancelot-eclipse/lancelot/resources/rules.xml'),
                java.net.URL('file:///home/edvard/dev/nr/lancelot-eclipse/lancelot/resources/wordnet-3-dict/'),
                java.io.File('../../lancelot/resources/manual_dict.txt'), 
                java.io.File('../../lancelot/resources/reverse_map.txt')
            )

    def __init__(self, zip_path):
        LancelotClassAnalyser.ensure_lancelot_initialized()
        self._zip_path = zip_path
    
    def run(self):
        reports, locations = [], {}
        for name, read_fn in zip_utils.get_entries(self._zip_path):
            if name.endswith('.class'):
                print 'Analysing %s...' % name
                bytecode = read_fn()
                try:
                    operation = ClassAnalysisOperation('', bytecode, 0)
                    report = operation.run()
                except:
                    print 'log problem'
                    continue
                if report.hasBugs():
                    #n += 1
                    #if n == 2:
                    #    break
                    method_locs = self._find_method_locations(report, bytecode)
                    locations.update(method_locs)
                    reports.append(report)
        return reports, locations

    def _find_method_locations(self, report, bytecode):
        locations = MethodLocationFinder.findMethodLocations(bytecode)
        res = {}
        for orig_key in locations:
            method_name, method_desc = orig_key.split('$$$')
            new_key = (report.packageName, report.className, 
                       method_name, method_desc,) 
            res[new_key] = locations[orig_key]
        return res

class ThinClassAnalysisReport:
    def __init__(self, package_name, class_name, method_bug_reports):
        self.package_name = package_name
        self.class_name = class_name
        self.method_bug_reports = method_bug_reports

    @staticmethod
    def from_full_report(full_report):
        return ThinClassAnalysisReport(
            full_report.packageName.replace('/', '.'),
            full_report.className,
            []
        )


class GalahadController:
    def __init__(self, analyser, locator, view_class):
        self._analyser = analyser
        self._locator = locator
        self._view_class = view_class
    
    def run(self):
        class_reports, method_locs = self._analyser.run()
        processed_reports = [
            self._locator.process_class_report(cr, method_locs)
            for cr in class_reports
        ]
        view = self._view_class(processed_reports)
        view.process()
        with open('out.html', 'w') as of:
            of.write(view.result)
    
    @staticmethod
    def create_default(binary_zip_path, source_zip_path):
        return GalahadController(LancelotClassAnalyser(binary_zip_path),
                                 SourceLocator(source_zip_path),
                                 HTMLReportRenderer)

def main():
    binary_zip_path = '/home/edvard/Downloads/fb/binary.jar'
    source_zip_path = '/home/edvard/Downloads/fb/source.zip'
    runner = GalahadController.create_default(binary_zip_path, source_zip_path)
    runner.run()

try:
    import time
    start = time.time()
    print 'Starting...'
    main()
finally:
    print time.time() - start

