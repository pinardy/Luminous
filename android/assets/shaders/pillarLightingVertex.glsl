#version 300 es

in vec4 a_position;
in vec4 a_color;
in vec2 a_texCoord0;

uniform mat4 u_worldView;
uniform vec4 u_worldColorPillarA;
uniform vec4 u_worldColorPillarB;
uniform vec2 u_lightPosPillarA;
uniform vec2 u_lightPosPillarB;

out vec2 v_texCoords;
out vec4 v_position;
out vec4 v_worldColorPillarA;
out vec4 v_worldColorPillarB;
out vec2 v_lightPosPillarA;
out vec2 v_lightPosPillarB;


void main() {

    v_worldColorPillarA = a_color;
    v_worldColorPillarB = a_color;

    v_worldColorPillarA = u_worldColorPillarA;
    v_worldColorPillarB = u_worldColorPillarB;
    v_lightPosPillarA = u_lightPosPillarA;
    v_lightPosPillarB = u_lightPosPillarB;

    v_position.xy = a_position.xy;
    v_texCoords = a_texCoord0;

    gl_Position = u_worldView * a_position;
}