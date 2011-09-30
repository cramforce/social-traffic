(function() {
  
  var Radius = 50;
  
  var Locations = {
    ap0: {"x":336,"y":600},
    ap1: {"x":777,"y":298},
    ap2: {"x":869,"y":696},
    ap3: {"x":336,"y":94},
    ap4: {"x":436,"y":168},
    ap5: {"x":344,"y":105},
    ap6: {"x":700,"y":89},
    ap7: {"x":854,"y":274},
    ap8: {"x":945,"y":334},
    ap9: {"x":680,"y":251},
    ap10: {"x":429,"y":261},
    ap11: {"x":436,"y":168},
    ap12: {"x":695,"y":151}
  };
  
  function position(user, point) {
    var ap = user.ap;
    var id = user.mac;
    var a = document.getElementById(id);
    if (!a) {
      a = document.createElement('a');
      a.id = id;
      a.href = 'https://twitter.com/' + encodeURIComponent(a.screenName);
      var image = new Image();
      image.src = 'images/' + user.image;
      image.width = 32;
      image.height = 32;
      a.appendChild(image);
      document.body.appendChild(a);
      a.style.position = 'absolute';
      a.style.display = 'block';
      a.style.top = 0;
      a.style.left = 0;
      a.style.zIndex = 2;
      a.style.webkitTransitionProperty = 'transform';
      a.style.webkitTransitionDuration = '2s';
      a.style.mozTransitionProperty = 'transform';
      a.style.mozTransitionDuration = '2s';
    }
    a.style.webkitTransform = 'translate(' + point.x + 'px, ' + point.y + 'px)';
    a.style.mozTransform = 'translate(' + point.x + 'px, ' + point.y + 'px)';
  }
  
  function pointsOnCircle(pointCount, radius, center) {
    var points = [];
    var slice = 2 * Math.PI / pointCount;
    for (var i = 0; i < pointCount; i++) {
      var angle = slice * i;
      var newX = (center.x + radius * Math.cos(angle));
      var newY = (center.y + radius * Math.sin(angle));
      points.push({
        x: newX,
        y: newY
      });
    }
    return points;
  }
  
  function load() {
    $.get('./data.json', function(data) {
      function draw() {
        var aps = {};
        var count = 0;
        data.forEach(function(user) {
          user.ap = 'ap' + Math.floor(Math.random() * 9);
          user.mac = count++;
          if (!aps[user.ap]) {
            aps[user.ap] = [];
          }
          aps[user.ap].push(user);
        });
        for (var ap in aps) {
          var points = pointsOnCircle(aps[ap].length, Radius, Locations[ap]);
          var i = 0;
          var div = $('#' + ap);
          if (div.length == 0) {
            div = $('<div/>');
            div.attr('id', ap);
            div.css({
              position: 'absolute',
              left: Locations[ap].x - Radius + 16 + 'px',
              top: Locations[ap].y - Radius + 16 + 'px',
              'border-radius': Radius + 'px',
              'background-color': 'red',
              width: Radius * 2  + 'px',
              height: Radius * 2 + 'px',
              opacity: .5
            });
            $('body').append(div);
          }
          aps[ap].forEach(function(user) {
            var point = points[i++];
          
            position(user, point);
          });
        }
      }
      draw();
      setInterval(draw, 5000);
    });
  }
  load();
  
  $('#floor-plan').click(function(e) {
    console.log(e);
    console.log(JSON.stringify({x: e.offsetX, y: e.offsetY}));
  });
})();