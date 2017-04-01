/* jshint esversion: 6, jquery: true, node: true */
"use strict";
/** 
 * Library for automatic layout of feynman diagrams. Both connected and
 * disconnected.
 * @see autofeyn
 * @see discfeyn
 */
/** 
 * Distributes points around an ellpise
 * @return
 * "x, y" coords of the index"th point on an ellipse.
 */
function ellipsis(index, total, width, height, x0 = 0, y0 = 0) {
  x0 += width / 2;
  y0 += height / 2;
  if (total == 1) {
    return x0 + "," + y0;
  }
  var a = width / 4;
  var b = height / 4;
  var t = (2 * Math.PI * index) / total;
  var x = Math.floor(a * Math.cos(t) + x0);
  var y = Math.floor(b * Math.sin(t) + y0);
  return x + "," + y;
}
/** Distributes points a long a line. */
function line(index, total, length, offset = 0) {
  return Math.floor(total > 1 ? offset + index * length / (total - 1) : offset + length / 2);
}
/**
 * @return
 * list of indices in ascending order of objects in list that 
 * have properties and values matching obj.
 */
function indicesOfObjs(list, obj) {
  var res = [];
  for (var i = 0; i < list.length; i++) {
    var matches = true;
    for (var prop in obj) {
      if (obj.hasOwnProperty(prop) &&
        (!list[i].hasOwnProperty(prop) ||
          list[i][prop] != obj[prop])) {
        matches = false;
      }
    }
    if (matches) {
      res.push(i);
    }
  }
  return res;
}
/** Splices all the indices from the array */
function multisplice(arr, idxs) {
  idxs.sort(function(a, b) {
    return b - a;
  });
  for (var i = 0; i < idxs.length; i++) {
    arr.splice(idxs[i], 1);
  }
}
/** 
 * @param feynList
 * an "autofeyn" list of paths, e.g.: ["v1-v2", "v1-v2-v5", ...]
 * @param type
 * the type of connection, e.g. "fermion"
 * @return 
 * a list of connection objects: [{from: "v1", to: "v2", type: "fermion"}, ...]
 */
function connections(feynList, type) {
  var conns = [];
  for (var i = 0; i < feynList.length; i++) {
    var vertices = feynList[i].split("-");
    var prev = vertices[0];
    for (var j = 1; j < vertices.length; j++) {
      conns.push({
        from: prev,
        to: vertices[j],
        type: type
      });
      prev = vertices[j];
    }
  }
  return conns;
}
/** @return a particle path list in reverse */
function revPath(path) {
  var newPath = [];
  for (var i = 0; i < path.length; i++) {
    newPath.push(path[i].split("-").reverse().join("-"));
  }
  return newPath;
}
/** @return a jquery.feyn particle path string in reverse */
function revFeynPath(path) {
  return revPath(path.split(",")).join(",");
}
/**
 * Automatically calculates lines and arcs for connections.
 * @param electrons, positrons, photons
 * an "autofeyn" particles object, e.g.:
 * {fermion: ["v1-v2", "v1-v2-v4"],
 *  photon: ["i1-v3-o1"]}
 * @return
 * an jquery.feyn particles object, e.g:
 * {fermion: {line: "v2-v4", arc: "v1-v2,v2-v1"},
 *  photon:  {line: "i1-v3,v3-o1", arc:""}}
 */
function particles(autofeynObj) {
  var conns = []; // List of connection objs
  var feynObj = {}; // Final result obj
  for (var type in autofeynObj) {
    if (autofeynObj.hasOwnProperty(type)) {
      conns = conns.concat(connections(autofeynObj[type], type));
      feynObj[type] = {
        line: "",
        arc: ""
      };
    }
  }

  function toFeynObj(idx, type) {
    feynObj[conns[idx].type][type] += conns[idx].from + "-" + conns[idx].to + ",";
  }
  while (conns.length > 0) {
    var idx1 = indicesOfObjs(conns, {
      "from": conns[0].from,
      "to": conns[0].to
    });
    var idx2 = indicesOfObjs(conns, {
      "from": conns[0].to,
      "to": conns[0].from
    });
    switch (idx1.length + "-" + idx2.length) {
      case "0-1":
        toFeynObj(idx2[0], "line");
        break;
      case "0-2":
        toFeynObj(idx2[0], "line");
        toFeynObj(idx2[1], "arc");
        break;
      case "1-0":
        toFeynObj(idx1[0], "line");
        break;
      case "1-1":
        toFeynObj(idx1[0], "arc");
        toFeynObj(idx2[0], "arc");
        break;
      case "1-2":
        toFeynObj(idx1[0], "arc");
        toFeynObj(idx2[0], "line");
        toFeynObj(idx2[1], "arc");
        break;
      case "2-0":
        toFeynObj(idx1[0], "line");
        toFeynObj(idx1[1], "arc");
        break;
      case "2-1":
        toFeynObj(idx1[0], "line");
        toFeynObj(idx1[1], "arc");
        toFeynObj(idx2[0], "arc");
        break;
      default:
        // Not possible for electromagnetic feynman diagrams
        break;
    }
    multisplice(conns, idx1.concat(idx2));
  }
  feynObj.photon.arc = revFeynPath(feynObj.photon.arc);
  return feynObj;
}
/** @return a fermion list from electron and positron lists */
function fermion(electron, positron) {
  return electron.concat(revPath(positron));
}
/** Draws the feynman diagram with auto resizing
 * and other default options.
 */
$.fn.drawfeyn = function(opts) {
  this.each(function() {
    var defOpts = {
      width: $(this).width(),
      height: $(this).height(),
      photon: {
        color: "blue"
      },
      node: {
        show: "v",
        fill: "black",
        radius: 5
      },
      thickness: 3,
    };
    var newOpts = Object.assign({}, defOpts, opts);
    newOpts.node = Object.assign({}, defOpts.node, opts.node);
    newOpts.photon = Object.assign({}, defOpts.photon, opts.photon);
    $(this).feyn(newOpts);
    var svg = $(this).children("svg:first");
    svg.attr("width", "100%");
    svg.attr("height", "100%");
  });
  return $(this);
};
/** 
 * Draws a feynman diagram with an automatic layout. 
 * @param opts 
 * Object of the form for jquery.feyn with the following
 * replacement parameters:
 * { 
 *   incoming: ['i1', 'i2', ],
 *   outgoing: ['o1', ],
 *   vertex:   ['v1', ],
 *   electron: ['i1-v1', ],
 *   positron: ['i2-v1', ],
 *   photon:   ['v1-o1', ],
 * }
 */
$.fn.autofeyn = function(opts) {
  this.each(function() {
    var width = opts.width === undefined ? $(this).width() : opts.width;
    var height = opts.height === undefined ? $(this).height() : opts.height;
    var newOpts = {
      incoming: {},
      outgoing: {},
      vertex: {},
    };
    // Incoming
    var i;
    for (i = 0; i < opts.incoming.length; i++) {
      newOpts.incoming[opts.incoming[i]] = "0," + line(i, opts.incoming.length, height);
    }
    // Outgoing
    for (i = 0; i < opts.outgoing.length; i++) {
      newOpts.outgoing[opts.outgoing[i]] = width + "," + line(i, opts.outgoing.length, height);
    }
    // Vertices
    for (i = 0; i < opts.vertex.length; i++) {
      newOpts.vertex[opts.vertex[i]] = ellipsis(i, opts.vertex.length, width, height);
    }
    // Connections
    opts.fermion = fermion(opts.electron, opts.positron);
    Object.assign(newOpts, particles({
      fermion: opts.fermion,
      photon: opts.photon
    }));
    delete opts.incoming;
    delete opts.outgoing;
    delete opts.vertex;
    delete opts.electron;
    delete opts.positron;
    delete opts.fermion;
    delete opts.photon;
    $(this).drawfeyn(Object.assign({}, newOpts, opts));
  });
  return $(this);
};
/** 
 * Draws a disconnected feynman diagram with an automatic layout. 
 * @param opts 
 * Object of the form for jquery.feyn with the following
 * replacement parameters:
 * { 
 *   innerWidth:  30, (optional)
 *   innerHeight: 30, (optional)
 *   incoming:    ['i1', 'i2', 'i3', ],
 *   outgoing:    ['o1', 'o2', ],
 *   vertex:      ['v1', 'v2', 'v3', 'v4', ],
 *   electron:    ['i1', 'o2', ],
 *   positron:    ['i2', 'i3', ],
 *   photon:      ['o1', ],
 * }
 */
$.fn.discfeyn = function(opts) {
  /* Adjusts vertices for additional incoming/outgoing vertices */
  function newVertex(v) {
    var index;
    index = opts.incoming.indexOf(v);
    if (index != -1) {
      return "v" + (index + 1);
    }
    index = opts.outgoing.indexOf(v);
    if (index != -1) {
      return "v" + (opts.incoming.length + index + 1);
    }
    var num = parseInt(v.match(/\d+/), 10);
    num += opts.incoming.length;
    num += opts.outgoing.length;
    return "v" + num;
  }
  this.each(function() {
    /* Draw the incoming and outgoing on an outer div, and the vertices
     * on an inner div.
     */
    var width = opts.width === undefined ? $(this).width() : opts.width;
    var height = opts.height === undefined ? $(this).height() : opts.height;
    var innerWidth = opts.innerWidth === undefined ? $(this).width() / 2 : opts.innerWidth;
    var innerHeight = opts.innerHeight === undefined ? $(this).height() / 2 : opts.innerHeight;
    // Outer div options
    var oopts = {
      incoming: {},
      outgoing: {},
      vertex: {},
      fermion: {
        line: ""
      },
      photon: {
        line: ""
      },
      scalar: {
        line: "",
        thickness: 1
      },
      node: {
        show: false
      },
    };
    var innerLeft = width / 2 - innerWidth / 2;
    var innerRight = width / 2 + innerWidth / 2;
    var innerTop = height / 2 - innerHeight / 2;
    var innerBottom = height / 2 + innerHeight / 2;
    // Incoming
    var i, vertex, len, innerLen;
    for (i = 0; i < opts.incoming.length; i++) {
      var incoming = opts.incoming[i];
      vertex = newVertex(incoming);
      len = line(i, opts.incoming.length, height);
      innerLen = line(i, opts.incoming.length, innerHeight, innerTop);
      oopts.incoming[incoming] = "0," + len;
      oopts.vertex[vertex] = innerLeft + "," + innerLen;
    }
    // Outgoing
    for (i = 0; i < opts.outgoing.length; i++) {
      var outgoing = opts.outgoing[i];
      vertex = newVertex(outgoing);
      len = line(i, opts.outgoing.length, height);
      innerLen = line(i, opts.outgoing.length, innerHeight, innerTop);
      oopts.outgoing[outgoing] = width + "," + len;
      oopts.vertex[vertex] = innerRight + "," + innerLen;
    }
    // Fermions - electrons
    for (i = 0; i < opts.electron.length; i++) {
      var electron = opts.electron[i];
      oopts.fermion.line += electron + "-" + newVertex(electron) + ",";
    }
    // Fermions - positrons
    for (i = 0; i < opts.positron.length; i++) {
      var positron = opts.positron[i];
      oopts.fermion.line += newVertex(positron) + "-" + positron + ",";
    }
    // Photons
    for (i = 0; i < opts.photon.length; i++) {
      var photon = opts.photon[i];
      oopts.photon.line += photon + "-" + newVertex(photon) + ",";
    }
    // Dashed line
    oopts.vertex[newVertex("v1")] = innerLeft + "," + innerTop;
    oopts.vertex[newVertex("v2")] = innerLeft + "," + innerBottom;
    oopts.vertex[newVertex("v3")] = innerRight + "," + innerBottom;
    oopts.vertex[newVertex("v4")] = innerRight + "," + innerTop;
    oopts.scalar.line += newVertex("v1") + "-" + newVertex("v2") + ",";
    oopts.scalar.line += newVertex("v2") + "-" + newVertex("v3") + ",";
    oopts.scalar.line += newVertex("v3") + "-" + newVertex("v4") + ",";
    oopts.scalar.line += newVertex("v4") + "-" + newVertex("v1") + ",";
    // Inner div options
    // Create and draw the divs
    var outer = jQuery("<div/>", {
      css: {
        "width": "100%",
        "height": "100%",
        "position": "relative",
      },
    }).appendTo($(this));
    outer.drawfeyn(oopts);
    // Add inner div element
    var iopts = {
      incoming: [],
      outgoing: [],
      vertex: opts.vertex,
      electron: [],
      positron: [],
      photon: []
    };
    var inner = jQuery("<div/>", {
      css: {
        "left": (innerLeft * 100 / width) + "%",
        "top": (innerTop * 100 / height) + "%",
        "width": (innerWidth * 100 / width) + "%",
        "height": (innerHeight * 100 / height) + "%",
        "position": "absolute",
      },
    }).appendTo($(outer));
    inner.autofeyn(iopts);
  });
  return $(this);
};