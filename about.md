---
layout: page
title: About
---

People that make it possible to have this project, contributors, collaborators, etc. (Alphabetical order):

{% assign contributors = site.data.contributors %}
<ul>
{% for contributor in contributors %}
    <li>{{ contributor.name }} [{{contributor.email}}]</li>
{% endfor %}
</ul>