def mapjoin(fn, col): 
    return ''.join(map(fn, col))

def _auto_indent(method):
    def _auto_indent(self, *nkw, **kw):
        self._indent()
        res = method(self, *nkw, **kw)
        self._unindent()
        return res
    return _auto_indent    


class HTMLReportRenderer:
    def __init__(self, class_reports):
        self._lines = []
        self._indention_level = 0
        self._class_reports = class_reports
    
    @property
    def result(self):
        return '\n'.join(self._lines)

    def process(self):
        for r in self._class_reports:
            self._emit_class_report(r)
    
    @_auto_indent
    def _emit_class_report(self, report):
        self._emit_el_start('div', None, ('classReport',))
        self._emit_class_data(report)
        for mr in report.method_bug_reports:
            self._emit_method_bug_report(mr)
        self._emit_el_end('div')
    
    @_auto_indent
    def _emit_class_data(self, report):
        self._emit_line('<h2>%s in package %s</h2>' % (report.class_name,
                                                       report.package_name,))
    @_auto_indent
    def _emit_method_bug_report(self, report):
        self._emit_el_start('div', None, ('methodBugReport',))
        self._emit_violations(report.violations)
        self._emit_source_code(report.source_code)
        self._emit_el_end('div')

    @_auto_indent
    def _emit_violations(self, violations):
        self._emit_el_start('ul', None, ('violations',))
        for v in violations:
            self._emit_violation(v)
        self._emit_el_end('ul')

    @_auto_indent
    def _emit_violation(self, violation):
        self._emit_el_start('li')
        self._emit_line(str(violation))
        self._emit_el_end('li')

    @_auto_indent
    def _emit_source_code(self, source_code):
        self._emit_el_start('code')
        self._lines.append(source_code)
        self._emit_el_end('code')
    
    def _emit_el_start(self, el_type, id_=None, classes=None):
        self._emit_line('<%s%s%s>' % (
            el_type,
            ' id="%s"' % id_ if not id_ is None else '',
            ' class="%s"' % ' '.join(classes) if not classes is None else '',
        ))

    def _emit_el_end(self, el_type):
        self._emit_line('</%s>' % el_type)

    def _emit_line(self, line):
        indentation = self._indention_level * '  '
        self._lines.append(indentation + line)
    
    def _indent(self):
        self._indention_level += 1

    def _unindent(self):
        self._indention_level -= 1
