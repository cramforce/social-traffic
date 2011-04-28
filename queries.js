function(doc) {
  
  var timeRound = 10 * 60 * 1000;
  
  for(var i = 0, len = doc.tuples.length; i < len; i++) {
    var tuple = doc.tuples[i];
    if (!tuple.srcIP || !tuple.dstIP) continue;
    
    var tp = doc._id.match(/(\d+)-(\d+)-(\d+)\.(\d\d)(\d\d)(\d\d)/);
    
    if(tp) {
      
      var date = new Date(tp[1], parseInt(tp[2])-1, tp[3], tp[4], tp[5], tp[6]);
      
      var time = date.getTime();
    
      time = time - time % timeRound;
    
      var val = parseInt(tuple.octets, 10);
    
      var key;
      if (tuple.srcIP.indexOf('.') == -1) { // No IP :)
        key = 'out-' + tuple.srcIP;
      }
      else if (tuple.dstIP.indexOf('.') == -1) { // No IP :)
        key = 'in-' + tuple.dstIP;
      }
    
      if (key) {
        emit(['total' + key], val);
        emit([key, time], val);
        emit([key.replace(/-.*$/, '-N'), time], val);
      }
    }
  }
}



function(keys, values, rereduce) {
  var total = 0
  for(var i = 0, len = values.length; i < len; i++) {
    var octets = values[i]
    total += parseInt(octets, 10);
  }
  return total;
}