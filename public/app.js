
(function() {
  
  var Feed_Url_Prefix = 'http://192.168.1.24:2911';
  
  var options = {
    xaxis: { mode: 'time' }
  };
  var Data = [];
  
  function Feed(label, url) {
    this.url   = url;
    this.label = label;
    this.data  = [];
    this.active = true;
  }
  
  Feed.prototype.add = function(newData) {
    this.data.push(newData);
  }
  
  Feed.prototype.shortTermCopy = function() {
    var feed = new this.constructor(this.label);
    feed.data = this.data.slice(-30);
    return feed;
  }
  
  Feed.prototype.attach = function(newData) {
    var self = this;
    var count = 0;
    
    function fetch() {
      $.getJSON(self.url, function(data) {
        self.data = [];
        function aggregate(row) {
          return [row[0], row[1] + row[2]];
        }
        self.longTerm = data.longTerm.map(aggregate);
        self.shortTerm = data.shortTerm.map(aggregate);
        console.log(self.data);
      });
    }
    
    setInterval(fetch, 10000);
    fetch();
  }
  
  
  
  $(function() {
    var longTermPlaceholder = $("#long-term-traffic");
    var shortTermPlaceholder = $("#short-term-traffic");
    function draw() {
      
      function onlyActive(item) {
        return item.active;
      }
      
      var active = Data.filter(function (item) {
        return item.active;
      });
      
      $.plot(longTermPlaceholder,  active.map(function(feed) { return { data: feed.longTerm } }), options);
      $.plot(shortTermPlaceholder, active.map(function(feed) { return { data: feed.shortTerm } }), options);
    }
    
    function addFeed(name, screenName) {
      var url = '/traffic/total' + (screenName ? '/'+encodeURIComponent(screenName) : '') + '?callback=?';

      var feed = new Feed(name, Feed_Url_Prefix + url, draw);
      Data.push(feed);
      feed.attach();
      
      var id = 'check-' + name;
      var ul = $('#followList');
      var li = $('<li />');
      var checkbox = $('<input />').attr({
        type: 'checkbox',
        id: id,
        checked: true
      });
      checkbox.change(function() {
        feed.active = this.checked;
        draw();
      });
      var label = $('<label/>').attr({
        'for': id
      });
      label.text(name);
      li.append(checkbox).append(label);
      ul.append(li);
      draw();
    }
    setInterval(draw, 500);
    
    addFeed('All');
    
    function addNamedFeed(name) {
      addFeed('In ' + name, name);
      //addFeed('Out ' + name, 'out-' + name);
    }
    
    $('#addUser').click(function() {
      var name = $('#user').val();
      addNamedFeed(name);
    });
    
    function formatBytes(bytes) {
      function round(a) {
        return Math.round(a * 10) / 10;
      }
      if(bytes < 1000) {
        return bytes;
      }
      if(bytes < 1000 * 1000) {
        return round(bytes / 1000) + ' KB';
      }
      if(bytes < 1000 * 1000 * 1000) {
        return round(bytes / 1000 / 1000) + ' MB';
      }
      if(bytes < 1000 * 1000 * 1000 * 1000) {
        return round(bytes / 1000 / 1000 / 1000) + ' GB';
      }
    }
    
    function drawRanking() {
      
      var All_Time_Url = Feed_Url_Prefix +
          '/traffic/top10?count=500&callback=?';
          
      $.getJSON(All_Time_Url, function(rows) {        
        var html = rows.map(function(row) {
          return '<tr><td class="name">' + row.screenName + '</td><td>' + formatBytes(row.inTotal + row.outTotal) + '</td></tr>';
        }).join('\n');

        $('#user-traffic tbody').html(html);
      });
      
    };
    
    $(document).delegate('.statTable tr', 'click', function(e) {
      var name = jQuery.trim($(this).find('.name').text());
      addNamedFeed(name);
    });
    
    
    $(function() {
      function d() {
        drawRanking();
      }
      setInterval(d, 10000);
      d();
    })
    
  });
})()