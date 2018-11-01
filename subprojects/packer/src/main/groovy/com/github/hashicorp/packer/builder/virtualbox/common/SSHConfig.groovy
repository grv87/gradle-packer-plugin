package com.github.hashicorp.packer.builder.virtualbox.common

import com.github.hashicorp.packer.engine.types.InterpolableObject

class SSHConfig extends InterpolableObject {
  Comm communicator.Config `mapstructure:",squash"`

  SSHHostPortMin    uint `mapstructure:"ssh_host_port_min"`
  SSHHostPortMax    uint `mapstructure:"ssh_host_port_max"`
  SSHSkipNatMapping bool `mapstructure:"ssh_skip_nat_mapping"`

  // These are deprecated, but we keep them around for BC
  // TODO(@mitchellh): remove
  SSHWaitTimeout time.Duration `mapstructure:"ssh_wait_timeout"`
}
