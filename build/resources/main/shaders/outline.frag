#version 330

uniform float time;
uniform float slider;
uniform vec2 resolution;
out vec4 O;
out vec2 U;
precision highp float;
out vec4 o_output;


/* skew constants for 3d simplex functions */
const float F3 =  0.3333333;
const float G3 =  0.1666667;

/* discontinuous pseudorandom uniformly distributed in [-0.5, +0.5]^3 */
vec3 random3(vec3 c) {
	float j = 4096.0*sin(dot(c,vec3(7.0, 59.4, 15.0)));
	vec3 r;
	r.z = fract(512.0*j);
	j *= .125;
	r.x = fract(512.0*j);
	j *= .125;
	r.y = fract(512.0*j);
	return r-0.5;
}

/* 3d simplex noise */
float simplex3(vec3 p) {
	/* 1. find current tetrahedron T and it's four vertices */
	/* s, s+i1, s+i2, s+1.0 - absolute skewed (integer) coordinates of T vertices */
	/* x, x1, x2, x3 - unskewed coordinates of p relative to each of T vertices*/

	/* calculate s and x */
	vec3 s = floor(p + dot(p, vec3(F3)));
	vec3 x = p - s + dot(s, vec3(G3));

	/* calculate i1 and i2 */
	vec3 e = step(vec3(0.0), x - x.yzx);
	vec3 i1 = e*(1.0 - e.zxy);
	vec3 i2 = 1.0 - e.zxy*(1.0 - e);

	/* x1, x2, x3 */
	vec3 x1 = x - i1 + G3;
	vec3 x2 = x - i2 + 2.0*G3;
	vec3 x3 = x - 1.0 + 3.0*G3;

	/* 2. find four surflets and store them in d */
	vec4 w, d;

	/* calculate surflet weights */
	w.x = dot(x, x);
	w.y = dot(x1, x1);
	w.z = dot(x2, x2);
	w.w = dot(x3, x3);

	/* w fades from 0.6 at the center of the surflet to 0.0 at the margin */
	w = max(0.6 - w, 0.0);

	/* calculate surflet components */
	d.x = dot(random3(s), x);
	d.y = dot(random3(s + i1), x1);
	d.z = dot(random3(s + i2), x2);
	d.w = dot(random3(s + 1.0), x3);

	/* multiply d by w^4 */
	w *= w;
	w *= w;
	d *= w;

	/* 3. return the sum of the four surflets */
	return dot(d, vec4(52.0));
}

float map(float nMin, float nMax, float n) {
	return (n - nMin) / (nMax - nMin);
}
float gain(float x, float p) {
	return x < .5 ?
		.5 * pow(2. * x, p) :
		1. - .5 * pow(2. * (1. - x), p);
}

float thickness = 2.;
float scale = .5;
float t = 0.;

void main()
{

   // out vec4 fragColor, in vec2 FragCoord

    vec4 fragColor = vec4(1.0, 0.0, 0.0, 1.0);

	vec2 uv00 = gl_FragCoord.xy / resolution.xy;
    vec2 uv11 = uv00 + thickness / resolution.xy;
    vec2 uv10 = vec2(uv11.x, uv00.y);
    vec2 uv01 = vec2(uv00.x, uv11.y);

    float n00 = simplex3(vec3(uv00 * scale, time * .02));
    float n01 = simplex3(vec3(uv01 * scale, time * .02));
    float n10 = simplex3(vec3(uv10 * scale, time * .02));
    float n11 = simplex3(vec3(uv11 * scale, time * .02));

    n00 = map(-1., 1., n00);
    n01 = map(-1., 1., n01);
    n10 = map(-1., 1., n10);
    n11 = map(-1., 1., n11);

    float f1 = 0.;

    //f1 = ((n00 < .5) != (n11 > .5)) && ((n01 < .5) != (n10 > .5)) ? 1. : 0.;

    for(float t = 0.; t < 1.; t += .02){
	    //f1 = max(f1, around(t, a));
        f1 = max(f1, ((n00 < t) != (n11 > t)) && ((n01 < t) != (n10 > t)) ? 0. : 1.);
    }

	o_output = vec4(vec3(f1)*slider, 1.0);

    //fragColor = min(fragColor, vec4(uv,1.,1.0));
}

//void main()
//{
//
//    O = vec4(10.0, 2.0, 1.0, 1.0);
//    U = vec2(10.0, 10.0);
//
//	U /= resolution.xy;
//    float n = simplex3( vec3( U * scale, time * .02)) * 35.;
//
//    o_output = vec4 ( smoothstep( 1.5, 0., abs(fract(n+.5)-.5) / fwidth(n) ) );
//    //o_output = vec4(1.0, 0.0, 0.0, 1.0);
//}
