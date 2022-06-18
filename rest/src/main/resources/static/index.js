THREE.IcosahedronGeometry = function(radius, detail, data) {

  var vertices = data.map(e => [e['x'], 5, e['y']]).flat(1);

  var v3 = new THREE.Vector3();
  var spherical = new THREE.Spherical();
  for (var i = 0; i < vertices.length; i += 3) {
    v3.fromArray(vertices, i);
    spherical.setFromVector3(v3);
    v3.setFromSpherical(spherical);
    v3.toArray(vertices, i);
  }

  THREE.PolyhedronGeometry.call(this, vertices,360,radius, detail);
  this.type = 'IcosahedronGeometry';
  this.parameters = {
    radius: radius,
    detail: detail
  };
};

THREE.IcosahedronGeometry.prototype = Object.create(THREE.PolyhedronGeometry.prototype);
THREE.IcosahedronGeometry.prototype.constructor = THREE.IcosahedronGeometry;

// Scene
var scene = new THREE.Scene();
var camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);

var renderer = new THREE.WebGLRenderer({
  antialias: 1
});

renderer.setClearColor(0xf7f7f7);
renderer.setSize(window.innerWidth, window.innerHeight);
document.body.appendChild(renderer.domElement);

scene.fog = new THREE.Fog(0xd4d4d4, 8, 20);

// Create vertex points
function run(radius, detail, data) {
  var mesh = new THREE.IcosahedronGeometry(radius, detail, data); // radius, detail
  var vertices = mesh.vertices;

  var positions = new Float32Array(vertices.length * 3);

  for (var i = 0, l = vertices.length; i < l; i++) {
    vertices[i].toArray(positions, i * 3);
  }
  var geometry = new THREE.BufferGeometry();
  geometry.addAttribute('position', new THREE.BufferAttribute(positions, 3));

  var material = new THREE.PointsMaterial({
    size: 0.4,
    vertexColors: THREE.VertexColors,
    color: 0x252525
  });
  var points = new THREE.Points(geometry, material);

  var object = new THREE.Object3D();

  object.add(points);

  object.add(new THREE.Mesh(
    mesh,
    new THREE.MeshPhongMaterial({
      color: 0x616161,
      emissive: 0xa1a1a1,
      wireframe: true,
      fog: 1
    })

  ));

  scene.add(object);

  camera.position.z = 20;

  var render = function() {
    requestAnimationFrame(render);

    object.rotation.x += 0.001;
    object.rotation.y += 0.001;

    renderer.render(scene, camera);
  };

  render();
}

fetch("http://localhost:8080/data").then(data => data.json()).then(data => run(10,2,data));