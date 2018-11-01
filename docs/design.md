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
