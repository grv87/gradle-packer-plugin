Best Practices
==============

In order to use Gradle up-to-date checking mechanism, some assumptions
and expectations are made on what exactly is done during Packer build.
These expectations and possible problems when they are not met
are listed in this document.

## Paths/file names/extensions of scripts

It is expected that scripts are independent of their location,
file names and extensions.
Variations in these should not affect script run result.

All related properties are excluded from up-to-date detection
with `@Staging` annotation.

## Conditions in scripts

Plugin is not able to parse shell scripts to detect
whether a script will actually do something, and what exactly.

So, the following approaches are not compatible
with up-to-date detection:

*   [Making provisioner steps conditional on the variable value
    with shell `if` command
    ](https://www.packer.io/docs/templates/user-variables.html#making-a-provisioner-step-conditional-on-the-value-of-a-variable)

*   Testing for `PACKER_BUILDER_TYPE` environment variable inside scripts
    like [this](
    https://github.com/chef/bento/blob/955f7a16f73089430c8c78fde02b6ab80388e2c0/_common/virtualbox.sh)

    Plugin assumes the provisioner is run each time
    regardless of the actual value of the variable.

    If the script is changed, all the builds will be marked
    as not-up-to-date, although, probably, the change
    didn't affect some of them.

    To limit the run of the script on specific builds
    `only` and `except` configurations should be used.

## `shell-local` provisioner and post-processor

Although they are supported, they should not have side effects.

One example of side effects is producing extra artifacts.
[This example from Packer documentation](
https://www.packer.io/docs/post-processors/shell-local.html#interacting-with-build-artifacts)
is not supported *from the box*.
Such extra artifacts won't be included in up-to-date detection,
and the task won't be rerun if artifact is changed or deleted
after last successful build.

Workarounds are:

*   Manually add extra artifacts to `PackerBuild` task outputs:
    ```
    task('packerBuild-file') {
        outputs.file 'artifacts.tgz'
    }
    ```

*   Use regular Gradle tasks and other features

Note that *most* provisioners, not only `shell-local`,
work on the machine created by builder and don't have their own outputs.
The known exception is `file` provisioner which exposes its outputs
with getters annotated with `@OutputFile` and `@OutputDirectory`.

## Timing of the connection or the build

All related properties are excluded from up-to-date detection
with `@Timing` annotation.

It is expected that, if these settings are changed
after successful build they can't change resulting artifacts,
so there is no sense to run rebuild.

## Rule for Maintainers and Developers of Custom Plugins

Keep in mind that up-to-date detection works
after successful build only.
So, determining whether some property should participate
in up-to-date detection or not, you have to answer the question:
could change in this property, assuming everything else is the same,
and the new build is successful too, produce different artifacts?


------------------------------------------------------------------------
Copyright © 2018-2019  Basil Peace

This file is part of gradle-packer-plugin.

This plugin is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation, either version 3
of the License, or (at your option) any later version.

This plugin is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU Lesser General Public Lise
along with this plugin.  If not, see <https://www.gnu.org/licenses/
