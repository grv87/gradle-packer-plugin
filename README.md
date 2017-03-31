# gradle-packer-plugin
This plugin allows to run [Packer](http://www.packer.io/) builds from Gradle.

1.	It parses Packer's templates and creates Gradle tasks to run Packer.
	For each template it creates single task to run all builders at once
	and separate task for each builder.

2.	It could pass used-defined variables to Packer.

3.	It configures tasks' inputs and outputs so that Gradle could detect
	tasks as UP-TO-DATE.

Example of use:
```groovy
plugins {
	id 'org.fidata.packer' version '2.4.0'
}

packer {
	customVariables = [
		'packer_output_dir': packerOutputDir,
		'aws_access_key': awsAccessKey,
		'aws_secret_key': awsSecretKey
	]
	template 'src/base_images/UbuntuServer16.04.1-amd64.json'
}
```

## Requirements
*	[Packer](http://www.packer.io/)
*	[AWS CLI](https://aws.amazon.com/cli/) – for Amazon builders

## Task names
Plugin creates series of tasks for cleaning and running Packer builds.
If template has a `name` variable, it is used instead of template
filename. High-level task names are:

*      `clean-<name variable or template filename>`
*      `build-<name variable or template filename>`
*      `clean-<name variable or template filename>-<build name>`
*      `build-<name variable or template filename>-<build name>`

## Supported Packer configurations:
*	Builders:
	*	`virtualbox-iso` and `virtualbox-ovf`.

		The following fields are considered as inputs:
		*	`source_path` (for `virtualbox-ovf`)
		*	`floppy_files`
		*	preseed file from `http_directory` (only one file is
		supported)
		*	`ssh_key_path`

	*	`amazon-ebs`

		Plugin queries AWS for `source-ami` or `source_ami_filter` and
saves results in Gradle's cache.

		The following fields are also considered as inputs:
		*	`ssh_private_key_file`
		*	`user_data_file`

*	Provisioners:

	`only`, `except` and `override` configurations are supported.

	*	`shell`.

		The following fields are considered as inputs:
		*	`script`
		*	`scripts`

	*	`chef-solo`.

		The following fields are considered as inputs:
		*	`config_template`
		*	`cookbook_paths`
		*	`data_bags_path`
		*	`encrypted_data_bag_secret_path`
		*	`environments_path`
		*	`roles_path`

*	Post-processors:

	`only` and `except` configurations are supported.

	*	`vagrant`.

		The following fields are considered as inputs:
		*	`vagrantfile_template`
		*	`include`

		`override` configuration is supported.

*	Functions:

	Now only a subset of Packer template functions is supported:
	*	`build_name`
	*	`build_type`
	*	`pwd`
	*	`template_dir`
	*	`timestamp`
	*	`uuid`

## Notes
1.	Gradle (as of 3.2) [can't handle URLs]
(https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:file(java.lang.Object)).

	However, it is usually safe to assume that ISOs in the web
are not changed.


------------------------------------------------------------------------
Copyright © 2016-2017  Basil Peace

This is part of gradle-packer-plugin.

Copying and distribution of this file, with or without modification,
are permitted in any medium without royalty provided the copyright
notice and this notice are preserved.  This file is offered as-is,
without any warranty.
