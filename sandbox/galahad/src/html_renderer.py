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
        self._emit_line('<p class="header">Class <span class="className">%s'
                        '</span> in package <span class="packageName">%s'
                        '</span></p>' %(
                         report.class_name, report.package_name,))
    @_auto_indent
    def _emit_method_bug_report(self, report):
        self._emit_el_start('div', None, ('methodBugReport',))
        self._emit_header(report)
        self._emit_match_data(report)
        self._emit_source_code(report.source_code)
        self._emit_el_end('div')

    @_auto_indent
    def _emit_header(self, report):    
        self._emit_line('<p class="methodName">%s</p>' % report.method_name)

    @_auto_indent
    def _emit_match_data(self, report):
        self._emit_el_start('div', None, ('matchData',))
        self._emit_phrase_text(report.best_matching_phrase)
        self._emit_rules_and_violations(report.rules, report.violations)
        self._emit_el_end('div')

    @_auto_indent
    def _emit_phrase_text(self, phrase):
        self._emit_line('<p class="phraseText"><span class="label">Matching '
                        'phrase:</span> <span class="text">%s</span></p>' % 
                        phrase.text)

    @_auto_indent
    def _emit_rules_and_violations(self, rules, violations):
        output = []
        for rule in rules:
            output.append((rule.attribute.name(), rule.severity.name(),
                           rule.ifSet(), rule in violations,))
        output.sort(lambda a, b: cmp(b[3], a[3]))
        self._emit_el_start('table', None, ('rulesAndViolations',))
        self._emit_rules_and_violations_th()
        for o in output:
            self._emit_rule_or_violation(o)
        self._emit_el_end('table')


    @_auto_indent     
    def _emit_rules_and_violations_th(self):
        self._emit_el_start('tr')
        self._indention_level += 1
        for prop in ('Attribute', 'Severity', 'If set',):
            self._emit_line('<th>%s</th>' % prop)
        self._indention_level -= 1
        self._emit_el_end('tr')
        
    @_auto_indent
    def _emit_rule_or_violation(self, rule_or_violation):
        classes = []
        if rule_or_violation[3]:
            classes.append('violation')
            severity = rule_or_violation[1]
            classes.append('violation' + severity[0] + severity[1:].lower())

        self._emit_el_start('tr', None, classes)
        self._indention_level += 1
        for prop in rule_or_violation[:3]:
            self._emit_line('<td>%s</td>' % prop)
        self._indention_level -= 1
        self._emit_el_end('tr')

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
