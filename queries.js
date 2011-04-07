function(doc) {
  
  var timeRound = 10 * 60 * 1000;
  
  for(var i = 0, len = doc.tuples.length; i < len; i++) {
    var tuple = doc.tuples[i];
    if (!tuple.srcIP || !tuple.dstIP) continue;
    
    var time = parseInt(doc._id, 10);
    
    time = time - time % timeRound;
    
    var val = parseInt(tuple.octets, 10);
    
    var key;
    if (tuple.srcIP.indexOf('172.16.143') == 0) {
      key = 'out-' + tuple.srcIP;
    }
    else if (tuple.dstIP.indexOf('172.16.143') == 0) {
      key = 'in-' + tuple.dstIP;
    }
    
    if (key) {
      emit(['total-' + key], val);
      emit([key, time], val);
      emit([key.replace(/\.\d+$/, '-N'), time], val);
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