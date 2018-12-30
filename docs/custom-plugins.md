Custom Plugins
==============

Packer allows to use custom plugins: builders, provisioners,
post-processors.
In order to use this plugin with custom Packer plugins,
corresponding Gradle library/plugin should be developed and registered.

Engine doesn't support providing with services descriptors in resources
or with static fields since this would contradict with nature
of Gradle plugins.
All registrations should be made explicitly in runtime.

TODO: See example ...
