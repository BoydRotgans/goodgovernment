#version 330


uniform sampler2D tex0;          // input channel. XX = 2D/Cube
in vec2 v_texCoord0;
out vec4 o_output;


uniform vec4 colors[12];

void main()
{

   vec2 step = 1.0 / textureSize(tex0, 0);
   vec4 O = vec4(0.0);
   for (int j = -1 ; j <=1 ; ++j) {
        for (int i = -1; i <= 1; ++i) {
            O += texture(tex0, v_texCoord0 + step * vec2(i, j));
        }
   }
   O /= 9.0;

    const int levels = 12;

    float v = sin(3.1415926535 * levels * length(O.xyz));

    float plateau = floor(length(O.xyz) * levels) / levels;

    int plateauIndex = min(levels-1, int(plateau * levels));

    vec3 plateauColor = colors[plateauIndex].rgb; // mix(vec3(1.0, 0.0, 0.0), vec3(0.0, 1.0, 0.0), plateau);

    O *= 1.-smoothstep(0., 0.8, .5*abs(v)/fwidth(v));

    O.a = 1.0;
    o_output = O + vec4(plateauColor, 0.0);


    //o_output = colors[  int(v_texCoord0.x*12 )];
    //o_output = texture(tex0, v_texCoord0);

}