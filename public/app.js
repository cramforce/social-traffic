
(function() {
  
  var Feed_Url_Prefix = 'http://127.0.0.1:5984';
  
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
    $.getJSON(self.url, function(data) {
      self.data = data.rows.map(function(row) {
        return [row.key[1], row.value];
      });
    });
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
      
      $.plot(longTermPlaceholder, active, options);
      
      var shortTermData = active.map(function(feed) { return feed.shortTermCopy(); })
      
      $.plot(shortTermPlaceholder, shortTermData, options);
    }
    
    function addFeed(name, key) {

      var url = '/traffic/_design/total_traffic/_view/10minutes?group=true&startkey=[%22' + encodeURIComponent(key) + '%22]&endkey=[%22' + encodeURIComponent(key) + '.%22]&callback=?';

      var feed = new Feed(name, Feed_Url_Prefix + url, draw);
      Data.push(feed);
      feed.attach();
      
      var id = 'check-' + key;
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
    
    addFeed('Inbound', 'in-172.16.143-N');
    addFeed('Outbound', 'out-172.16.143-N');
    
    function addNamedFeed(name) {
      addFeed('In ' + name, 'in-' + name);
      addFeed('Out' + name, 'out-' + name);
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
    
    function drawRanking(type) {
      
      var All_Time_Url = Feed_Url_Prefix +
          '/traffic/_design/total_traffic/_view/10minutes?group=true&startkey=["' + type + '172.16.143"]&endkey=["' + type + '172.16.143X"]&callback=?';
          
      $.getJSON(All_Time_Url, function(data) {
        var rows = data.rows.sort(function(a, b) {
          return b.value - a.value;
        });
        
        var html = rows.map(function(row) {
          return '<tr><td class="name">' + row.key[0].replace(type, '') + '</td><td>' + formatBytes(row.value) + '</td></tr>';
        }).join('\n');

        $('#' + type + 'traffic tbody').html(html);
      });
      
    };
    
    $(document).delegate('.statTable tr', 'click', function(e) {
      var name = jQuery.trim($(this).find('.name').text());
      addNamedFeed(name);
    });
    
    
    $(function() {
      drawRanking('total-out-');
      drawRanking('total-in-');
    })
    
  });
})()