import re
import itertools
import jarray
import java.io
from java.io import BufferedInputStream, ByteArrayInputStream
import java.net
from java.util.zip import ZipFile
from no.nr.lancelot.frontend import LancelotRegistry, ClassAnalysisOperation
from html_renderer import HTMLReportRenderer
from japa.parser import JavaParser
import japa.parser.ast.visitor
from japa.parser.ast.visitor import GenericVisitorAdapter
from japa.parser.ast.body import TypeDeclaration, MethodDeclaration

def _create_read_fn(zip_file, entry):
    def read_entry():
        is_ = None
        try:
            is_ = BufferedInputStream(zip_file.getInputStream(entry))
            size = entry.getSize()
            data = jarray.zeros(size, 'b')
            nread_tot = 0
            while nread_tot < size:
                nread = is_.read(data, nread_tot, size-nread_tot)
                if nread <= 0:
                    raise java.io.IOException('Read ended prematurely')
                nread_tot += nread
            return data
        finally:
            if not is_ is None:
                is_.close()
    return read_entry

def get_entries(zip_path):
    zip_file = ZipFile(zip_path)
    return ((e.getName(), _create_read_fn(zip_file, e),) 
            for e in zip_file.entries())

def files_matching_postfix(entry_list, postfix):
    return itertools.ifilter(lambda e: e[0].endswith(postfix), entry_list)
