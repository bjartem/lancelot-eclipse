load('common.js');

var createAnalyzer = function(){
  var _tagger = new CachingTagger();
  var _rulebook = LancelotRegistry.getInstance().getRulebook();

  var _bugMap = {};

  return {
    getBugMap: function(){ return _bugMap; },

    analyze: function(classFilename){
      var csa = new ClassStreamAnalyzer();
      var class_ = csa.analyze(new ByteArrayInputStream(read(classFilename)));

      var allMethods = []
      var coveredMethods = [];
      var buggyMethods = [];

      class_.getMethods().forEach(function(method){
        var idea = ClassAnalysisOperation.deriveIdea(method, _tagger);
        var bestMatchingPhrase = _rulebook.find(idea);

        var rules = bestMatchingPhrase.getRules();
        var bugs = [];

        rules.toArray().forEach(function(rule){
          if (rule.covers(method.getSemantics())) 
              bugs.push(rule.getAttribute());
        });

        if (bugs.length > 0) {
            bugs.sort();
            var key = class_.getQualifiedName() + '.' + method.getMethodName();
            _bugMap[key] = bugs;
        }
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

  var bugMap = analyzer.getBugMap();

  var bugMapKeys = []
  for (var k in bugMap)
      bugMapKeys.push(k);
  bugMapKeys.sort();
  
  bugMapKeys.forEach(function(key){
    print(key + '$$' + bugMap[key].join(':'));
  });
};

main(arguments);
