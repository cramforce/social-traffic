
(function() {
  var options = {
    xaxis: { mode: 'time' }
  };
  var Data = [];
  
  function Feed(label, draw) {
    Data.push(this);
    this.label = label;
    this.data  = [];
    this.draw  = draw;
  }
  
  Feed.prototype.add = function(newData) {
    this.data.push(newData);
  }
  
  Feed.prototype.attach = function(newData) {
    var self = this;
    setInterval(function() {
      self.add([(new Date).getTime(), Math.random() * 10])
    }, 1000);
  }
  
  $(function() {
    var placeholder = $("#traffic");
    function draw() {
      $.plot(placeholder, Data, options);
    }
    
    draw();
    setInterval(draw, 500);
    
    var feed = new Feed('Data', draw);
    feed.attach();
    
    $('#addUser').click(function() {
      var name = $('#user').val();
      var feed = new Feed(name, draw);
      feed.attach();
    });
    
    (function() {
      var data = [
        ['hblank', 2048],
        ['cramforce', 1024],
        ['fastandfearless', 500],
        ['chris', 400],
        ['foo', 200],
        ['bar', 100]
      ];
      
      var html = data.map(function(ele) {
        return '<tr><td>' + ele[0] + '</td><td>' + ele[1] + '</td></tr>';
      }).join('\n');
      
      $('#userTraffic tbody').html(html);
    })();
    
  });
})()