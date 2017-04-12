#version 300 es

in vec4 a_position;
in vec4 a_color;
in vec2 a_texCoord0;

uniform mat4 u_worldView;
//for player
uniform vec4 u_worldColorPlayer;
uniform vec2 u_lightPosPlayer;
//for pillar
uniform vec4 u_worldColorPillarA;
uniform vec2 u_lightPosPillarA;
uniform vec4 u_worldColorPillarB;
uniform vec2 u_lightPosPillarB;

out vec2 v_texCoords;
out vec4 v_position;
//for player
out vec4 v_colorPlayer;
out vec2 v_lightPosPlayer;
//for pillar
out vec4 v_colorPillarA;
out vec2 v_lightPosPillarA;
out vec4 v_colorPillarB;
out vec2 v_lightPosPillarB;


void main() {

    v_colorPlayer = a_color;
    v_colorPillarA = a_color;
    v_colorPillarB = a_color;

    //for player
    v_colorPlayer = u_worldColorPlayer;
    v_lightPosPlayer = u_lightPosPlayer;

    //for pillar
    v_colorPillarA = u_worldColorPillarA;
    v_lightPosPillarA = u_lightPosPillarA;
    v_colorPillarB = u_worldColorPillarB;
    v_lightPosPillarB = u_lightPosPillarB;

    v_position.xy = a_position.xy;
    v_texCoords = a_texCoord0;

    gl_Position = u_worldView * a_position;
}


