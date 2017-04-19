#version 300 es

in vec4 a_position;
in vec4 a_color;
in vec2 a_texCoord0;

uniform mat4 u_worldView;

uniform vec4 u_worldColorPlayer;
uniform vec2 u_lightPosPlayer;

uniform vec4 u_worldColorPillarA;
uniform vec4 u_worldColorPillarB;
uniform vec4 u_worldColorPillarC;
uniform vec4 u_worldColorPillarD;

uniform vec2 u_lightPosPillarA;
uniform vec2 u_lightPosPillarB;
uniform vec2 u_lightPosPillarC;
uniform vec2 u_lightPosPillarD;

out vec2 v_texCoords;
out vec4 v_position;

out vec4 v_colorPlayer;
out vec2 v_lightPosPlayer;

out vec4 v_worldColorPillarA;
out vec4 v_worldColorPillarB;
out vec4 v_worldColorPillarC;
out vec4 v_worldColorPillarD;

out vec2 v_lightPosPillarA;
out vec2 v_lightPosPillarB;
out vec2 v_lightPosPillarC;
out vec2 v_lightPosPillarD;

void main() {

    //init colours
    v_colorPlayer = a_color;
    v_worldColorPillarA = a_color;
    v_worldColorPillarB = a_color;
    v_worldColorPillarC = a_color;
    v_worldColorPillarD = a_color;

    //for player
    v_colorPlayer = u_worldColorPlayer;
    v_lightPosPlayer = u_lightPosPlayer;

    //for pillar
    v_worldColorPillarA = u_worldColorPillarA;
    v_worldColorPillarB = u_worldColorPillarB;
    v_worldColorPillarC = u_worldColorPillarC;
    v_worldColorPillarD = u_worldColorPillarD;

    v_lightPosPillarA = u_lightPosPillarA;
    v_lightPosPillarB = u_lightPosPillarB;
    v_lightPosPillarC = u_lightPosPillarC;
    v_lightPosPillarD = u_lightPosPillarD;

    v_position.xy = a_position.xy;
    v_texCoords = a_texCoord0;

    gl_Position = u_worldView * a_position;
}


