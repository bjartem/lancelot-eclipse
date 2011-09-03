importClass(java.io.File);
importClass(java.io.FileInputStream);
importClass(java.io.BufferedInputStream);
importClass(java.io.ByteArrayInputStream);
importClass(java.net.URL);
importClass(Packages.no.nr.lancelot.frontend.ClassAnalysisOperation);
importClass(Packages.no.nr.lancelot.frontend.LancelotRegistry);
importClass(Packages.no.nr.lancelot.tagging.CachingTagger);
importClass(Packages.no.nr.lancelot.analysis.code.asm.ClassStreamAnalyzer);

var createJavaArray = function(type, length){
    return java.lang.reflect.Array.newInstance(type, length);
};

var read = function(filename){
  var file = new File(filename);
  var is = new BufferedInputStream(new FileInputStream(file));
  var length = file.length();

  var bytes = createJavaArray(java.lang.Byte.TYPE, length);

  var offset = 0,
      numRead = 0;
  while (offset < bytes.length 
  && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) 
    offset += numRead;

  if (offset < bytes.length) 
    throw 'Could not completely read file ' + filename;
  
  is.close();
  return bytes;
};

var initializeLancelot = function(resourcesDir){
  var resourcesUrl = function(n){ 
      return new URL('file://' + resourcesDir + '/' + n);
  };
  var resourcesFile = function(n){ 
      return new File(resourcesDir + '/' + n);
  };

  LancelotRegistry.initialize(
    resourcesUrl('rules.xml'),
    resourcesUrl('wordnet-3-dict'),
    resourcesFile('manual_dict.txt'),
    resourcesFile('reverse_map.txt')
  );
};
