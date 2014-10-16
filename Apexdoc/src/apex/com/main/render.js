$(document).ready(function(){
    var total = 0;
    var content = $('#content');
    for(var i = 0; i < javadoc.classes.length; i++){
      var apexClass = javadoc.classes[i];
      if(apexClass.name.indexOf("Test") < 0 && apexClass.name.indexOf("Coverage") < 0){
        var container = $('<div class="class"></div>');
        $('<div class="name">' + $('<div></div>').text(apexClass.name).text() + '</div>').appendTo(container);
        $('<div class="author">' + $('<div></div>').text(apexClass.author).text() + '</div>').appendTo(container);
        $('<div class="description">' + apexClass.description + '</div>').appendTo(container);
        var methodsContainer = $('<div class="methods"></div>');
        var hasMethods = false;

        for(var z = 0; z < apexClass.constructors.length; z++){
          var method = apexClass.constructors[z];
          if(method.name != ""){
            var mContainer = $('<div class="method"></div>');
            $('<div class="name">' + $('<div></div>').text(method.name).html() + '<div class="author">' + method.author + '</div></div>').appendTo(mContainer);
            var pContainer = $('<div class="params"></div>');
            for(var y = 0; y < method.params.length; y++){
              $('<div class="param"><span class="name">' + method.params[y].name + '</span><span class="description">' + method.params[y].description + '</span></div>').appendTo(pContainer);
            }
            pContainer.appendTo(mContainer);
            $('<div class="description">' + method.description + '</div>').appendTo(mContainer);
            mContainer.appendTo(methodsContainer);
            hasMethods = true;
          }
        }
        for(var z = 0; z < apexClass.methods.length; z++){
          var method = apexClass.methods[z];
          /*if(method.description != ""){*/
            var mContainer = $('<div class="method"></div>');
            var needsParams = false;
            if(method.name.indexOf("()") < 0 && method.params.length == 0){
              needsParams = true;
            }
            $('<div class="name" ' + (needsParams ? 'style="color: red;" ' : '' ) + '>' + $('<div></div>').text(method.name).html() + '<div class="author">' + method.author + '</div></div>').appendTo(mContainer);
            var pContainer = $('<div class="params"></div>');
            for(var y = 0; y < method.params.length; y++){
              $('<div class="param"><span class="name">' + method.params[y].name + '</span><span class="description">' + method.params[y].description + '</span></div>').appendTo(pContainer);
            }
            pContainer.appendTo(mContainer);
            $('<div class="description">' + method.description + '</div>').appendTo(mContainer);
            mContainer.appendTo(methodsContainer);
            hasMethods = true;
          //}
        }
        if(hasMethods){
          methodsContainer.appendTo(container);
        }
        container.appendTo(content);
        total++;
      }
    }
  });