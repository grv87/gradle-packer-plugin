Design
======

## Interpolation

Run is made in several stages:
1.  Interpolation of user variables
2.  Interpolation of builder headers
    (there is only one interpolable string, build name)
3.  Interpolation of the whole builder,
    and also provisioners and post-processors
4.  Determining of output artifacts

The first two stages are implemented in `Template.doInterpolate`.
The third stage is implemented in the following methods:
*   `Template.interpolateForBuilder`
*   `Builder.doInterpolate`
*   `Provisioner.interpolateForBuilder`
*   `PostProcessor.interpolateForBuilder`

The forth stage is implemented in the following methods:
*   `Template.interpolateForBuilder`
*   `Builder.run`
*   `PostProcessor.postProcess`

When plugin is applied to `Settings` and tasks are created automatically
then stages 1 and 2 are passed before task creation. // TODO
Stage 3 is passed when Gradle detects task inputs/outputs
before its run.

## Inputs

Not considered as inputs:
*   timing settings
*   passwords
*   connection settings
*   staging/temp paths on images

Considered:
*   paths on images (except staging/temp)
*   users under which provisioning is happening, sudo settings

## Paths

Context gets `cwd` resolved relatively to project dir already,
and after that `Project#file` and `Project#dir` methods
are not necessary.
Other methods (`Project#files` and `Project#fileTree`) get paths
resolved to `cwd`, so that project dir doesn't mess with them.

Usually classes (builders, provisioners, post-processors)
have properties of `InterpolableInputDirectory`,
`InterpolableInputRegularFile`, `InterpolableInputURI` types.
Their interpolated values are marked as Gradle task inputs.

`InterpolablePath` and `InterpolableURI` types are used
in the following situations only:

1.  Task outputs

    Builders, provisioners, post-processors usually don't expose
    output files and directories as properties.

    All regular outputs of builders and post-processors are handled
    by separate mechanism, with `Artifact` instances.
    The only known exception is `File` provisioner
    which could download files to local machine.

    So, there is no reason to have separate classes for outputs.

2.  Paths that are not considered as inputs, such as local cache

    They are marked with `@Internal` annotation at whole,
    so that annotation on `interpolatedValue` field is irrelevant.


------------------------------------------------------------------------
Copyright © 2018  Basil Peace

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
