package org.fidata.virtualbox

import org.fidata.packer.engine.types.InterpolableString

final class VBoxManageUtils {
  static getCpusUsed(List<List<String>> vboxManageCommands, Integer originalCpus) {
    boolean cpuHotPlugEnabled = false
    int cpus = originalCpus ?: 1
    int cpusHotPlugged = 1
    vboxManageCommands.each { List<String> vboxManageCommand ->
      if (vboxManageCommand.size() == 3 && vboxManageCommand[0] == 'modifyvm') {
        switch (vboxManageCommand[1]) {
          case '--cpuhotplug':
            switch (vboxManageCommand[2]) { // TODO
              case 'on':
                cpuHotPlugEnabled = true
                break
              case 'off':
                cpuHotPlugEnabled = false
                break
              default:
                // TODO: Warning / Exception
                null
            }
            break
          case '--cpus':
            cpus = vboxManageCommand[2].toInteger()
            break
          case '--plugcpu':
            cpusHotPlugged += vboxManageCommand[2].toInteger()
            break
          case '--unplugcpu':
            cpusHotPlugged -= vboxManageCommand[2].toInteger()
            break
        }
      }
    }
    cpuHotPlugEnabled ? Integer.min(cpusHotPlugged, cpus) : cpus
  }

  private VBoxManageUtils() {}
}
