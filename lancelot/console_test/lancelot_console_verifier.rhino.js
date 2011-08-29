importClass(java.io.File);
importClass(java.io.FileInputStream);
importClass(java.io.BufferedInputStream);
importClass(java.io.ByteArrayInputStream);
importClass(java.net.URL);
importClass(Packages.no.nr.lancelot.analysis.ClassAnalysisOperation);
importClass(Packages.no.nr.lancelot.analysis.LancelotRegistry);
importClass(Packages.no.nr.einar.naming.tagging.CachingTagger);
importClass(Packages.no.nr.einar.pb.analysis.code.asm.ClassStreamAnalyzer);

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

var initializeLancelot = function(currentDir){
  var resourcesUrl = function(n){ 
      return new URL('file://' + currentDir + '/../resources/' + n);
  };
  var resourcesFile = function(n){ 
      return new File(currentDir + '/../resources/' + n);
  };

  LancelotRegistry.initialize(
    resourcesUrl('rules.xml'),
    resourcesUrl('wordnet-3-dict'),
    resourcesFile('manual_dict.txt'),
    resourcesFile('reverse_map.txt')
  );
};

var createAnalyzer = function(){
  var _tagger = new CachingTagger();
  var _rulebook = LancelotRegistry.getInstance().getRulebook();

  var _numMethodsTotal = 0,
      _numMethodsCovered = 0,
      _numMethodsBuggy = 0;

  return {
    getNumMethodsTotal:   function(){ return _numMethodsTotal;   },
    getNumMethodsCovered: function(){ return _numMethodsCovered; },
    getNumMethodsBuggy:   function(){ return _numMethodsBuggy;   },

    analyze: function(classFilename){
      var csa = new ClassStreamAnalyzer();
      var javaClass = csa.analyze(new ByteArrayInputStream(read(classFilename)));

      var allMethods = []
      var coveredMethods = [];
      var buggyMethods = [];

      javaClass.getMethods().forEach(function(method){
        _numMethodsTotal++;

        var idea = ClassAnalysisOperation.deriveIdea(method, _tagger);
        var bestMatchingPhrase = _rulebook.findMatchingPhrase(idea);

        var rules = bestMatchingPhrase.getRules();
        var isCovered = !rules.isEmpty();

        if (isCovered)
          _numMethodsCovered++;
        
        var isBuggy = false;
        rules.toArray().forEach(function(rule){
          if (rule.covers(method.getSemantics())) 
            isBuggy = true;
        });

        if (isBuggy)
          _numMethodsBuggy++;

        print(method);
        print(bestMatchingPhrase.constructSignature());
        print(' ');
        //java.lang.Thread.sleep(500);
      });
    }
  };
};

var main = function(args){
  var currentDir = args[0];
  var classFilenames = args.slice(1);
  initializeLancelot(currentDir);

  var analyzer = createAnalyzer();

  print('Analyzing ' + classFilenames.length + ' classes...');
  classFilenames.forEach(analyzer.analyze);

  print('Total number of methods:   ' + analyzer.getNumMethodsTotal()   + '.');
  print('Number of covered methods: ' + analyzer.getNumMethodsCovered() + '.');
  print('Number of buggy methods:   ' + analyzer.getNumMethodsBuggy()   + '.');

};

main(arguments);
