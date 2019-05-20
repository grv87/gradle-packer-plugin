Deprecated Packer Features
==========================

Some Packer configuration options are already deprecated at the time
of writing plugin.
For simplification, only some of these configuration options
are supported.
This list documents what

It is recommended to validate your Packer template and, if necessary,
fix it manually or automatically with `packer fix`.

# Supported features

## Provisioners

### `shell-local`

*   `{{.Command}}` template variable

## Post-Processors

### `shell-local`

*   `{{.Command}}` template variable

# Unsupported features

## Post-Processors

### `shell-local`

*   Passing string instead of string instead of array of strings
    for `execute_command` configuration options


------------------------------------------------------------------------
Copyright ©  Basil Peace

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
