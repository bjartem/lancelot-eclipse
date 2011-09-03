load('common.js');

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
        var bestMatchingPhrase = _rulebook.find(idea);

        var rules = bestMatchingPhrase.getRules();
        var isCovered = bestMatchingPhrase.getPhraseText() != '*';

        if (isCovered)
          _numMethodsCovered++;
        
        var isBuggy = false;
        rules.toArray().forEach(function(rule){
          if (rule.covers(method.getSemantics())) 
            isBuggy = true;
        });

        if (isBuggy)
          _numMethodsBuggy++;
      });
    }
  };
};

var main = function(args){
  var resourcesDir = args[0];
  var classFilenames = args.slice(1);
  initializeLancelot(resourcesDir);

  var analyzer = createAnalyzer();
  classFilenames.forEach(analyzer.analyze);
  print(
      ''  + analyzer.getNumMethodsTotal() 
    + ' ' + analyzer.getNumMethodsCovered() 
    + ' ' + analyzer.getNumMethodsBuggy()
  );
};

main(arguments);
