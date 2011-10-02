(function() {
  
  var BASE_URL = 'http://10.109.1.9:2911';
  
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
    apa: {"x":429,"y":261},
    apb: {"x":436,"y":168},
    apc: {"x":695,"y":151},
    apd: {"x":336,"y":600},
    ape: {"x":777,"y":298},
    apf: {"x":869,"y":696},
    apg: {"x":336,"y":94},
    aph: {"x":436,"y":168},
    api: {"x":344,"y":105},
    apj: {"x":700,"y":89},
    apk: {"x":854,"y":274},
    apl: {"x":945,"y":334},
    apm: {"x":680,"y":251},
    apn: {"x":429,"y":261},
    
    "00272212ac23": {"x":76,"y":18},
    "00272212abaa": {"x":76,"y":201},
    "00272212ab0e": {"x":251,"y":81},
    "00272212ac21": {"x":278,"y":312},
    "00272212ac49": {"x":46,"y":492},
    "00272212abc6": {"x":181,"y":490},
    "00272212ac65": {"x":363,"y":604},
    "00272212ab88": {"x":628,"y":532},
    "00272212ac68": {"x":619,"y":654},
    "00272212ab99": {"x":907,"y":528},
    "00272212ab9b": {"x":907,"y":671},
    "00272212ac54": {"x":904,"y":375},
    "00272212aba0": {"x":899,"y":266},
    "00272212ac4e": {"x":717,"y":384},
    "00272212ab98": {"x":637,"y":141},
    "00272212abba": {"x":703,"y":65},
    "00272212ab68": {"x":400,"y":371},
    "00272212ab78": {"x":398,"y":164},
    "00272212ac59": {"x":311,"y":69}
    /*00272212abc3
    00272212abd1*/
  };
  
  function position(user, point) {
    var ap = user.ap;
    var id = user.mac;
    var a = document.getElementById(id);
    if(user.image) {
      console.log(user)
      if (!a) {
        a = document.createElement('a');
        a.id = id;
        a.href = 'https://twitter.com/' + encodeURIComponent(user.screenName);
        var image = new Image();
        image.src = user.image;
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
    } else {
      console.log('Missing image ', user);
    }
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
    $.getJSON(BASE_URL + '/streamies?callback=?', function(data) {
      function draw() {
        var aps = {};
        var count = 0;
        data.forEach(function(userData) {
          userData.clients.forEach(function(client) {
            var accessPoints = client.accessPoints;
            if(accessPoints && accessPoints.length > 0) {
              var user = {
                screenName: userData.screenName,
                ap: accessPoints.shift().toLowerCase().replace(/^v|h/, '').replace(/\:/g, ''),
                image: userData.details.profile_image_url_https
              };
              user.mac = client.hwaddr;
              if (!aps[user.ap]) {
                aps[user.ap] = [];
              }
              aps[user.ap].push(user);
              if(!Locations[user.ap]) {
                console.log('Missing ' + user.ap);
              }
              console.log(user.ap)
            }
          });
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
    });
  }
  load();
  
  function interval() {
    setTimeout(function() {
      load();
      interval();
    }, 5000)
  }
  interval();
  
  $('#floor-plan').click(function(e) {
    console.log(e);
    console.log(JSON.stringify({x: e.offsetX, y: e.offsetY}));
  });
})();