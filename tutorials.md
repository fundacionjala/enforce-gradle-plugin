---
layout: page
title: Tutorials
---
<div class="home">

  <ul class="post-list">
    {% for post in site.posts %}
      <li>
        <h2>
          <a class="post-link" href="{{ site.url }}{{ post.url }}">{{ post.title }}</a>
        </h2>
      </li>
    {% endfor %}
  </ul>

  <p class="rss-subscribe">subscribe <a href="{{ "/atom.xml" | prepend: site.url }}">via RSS</a></p>

</div>
