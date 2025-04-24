#version 330 core

in vec3 vertexColor;
in vec2 TextCoord;
out vec4 FragColor;
uniform sampler2D tex;

void main()
{
    FragColor = texture(tex, TextCoord);
}
