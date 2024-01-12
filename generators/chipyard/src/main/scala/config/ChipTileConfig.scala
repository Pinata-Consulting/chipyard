package chipyard

import org.chipsalliance.cde.config.{Config}
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.subsystem.{MBUS, SBUS}
import testchipip.soc.{OBUS}

// A simple config demonstrating how to set up a basic chip in Chipyard
class ChipLikeBoomTileConfig extends Config(
  
  //==================================
  // Set up TestHarness
  //==================================
  new chipyard.harness.WithAbsoluteFreqHarnessClockInstantiator ++ // use absolute frequencies for simulations in the harness
                                                                   // NOTE: This only simulates properly in VCS

  //==================================
  // Set up tiles
  //==================================
  new freechips.rocketchip.subsystem.WithAsynchronousRocketTiles(depth=8, sync=3) ++ // Add async crossings between RocketTile and uncore
  new boom.common.WithNMegaBooms(1) ++                             // 1 MegaBoom

  //==================================
  // Set up I/O
  //==================================
  new testchipip.serdes.WithSerialTLWidth(4) ++                                         // 4bit wide Serialized TL interface to minimize IO
  new testchipip.serdes.WithSerialTLMem(size = (1 << 30) * 4L) ++                       // Configure the off-chip memory accessible over serial-tl as backing memory
  new freechips.rocketchip.subsystem.WithNoMemPort ++                                   // Remove axi4 mem port
  new freechips.rocketchip.subsystem.WithNMemoryChannels(1) ++                          // 1 memory channel

  //==================================
  // Set up buses
  //==================================
  new testchipip.soc.WithOffchipBusClient(MBUS) ++                                      // offchip bus connects to MBUS, since the serial-tl needs to provide backing memory
  new testchipip.soc.WithOffchipBus ++                                                  // attach a offchip bus, since the serial-tl will master some external tilelink memory

  //==================================
  // Set up clock./reset
  //==================================
  //new chipyard.clocking.WithPLLSelectorDividerClockGenerator ++   // Use a PLL-based clock selector/divider generator structure

  // Create the uncore clock group
  new chipyard.clocking.WithClockGroupsCombinedByName(("uncore", Seq("implicit", "sbus", "mbus", "cbus", "system_bus", "fbus", "pbus"), Nil)) ++

    // The HarnessBinders control generation of hardware in the TestHarness
  new chipyard.harness.WithUARTAdapter ++                          // add UART adapter to display UART on stdout, if uart is present
  new chipyard.harness.WithBlackBoxSimMem ++                       // add SimDRAM DRAM model for axi4 backing memory, if axi4 mem is enabled
  new chipyard.harness.WithSimTSIOverSerialTL ++                   // add external serial-adapter and RAM
  new chipyard.harness.WithSimJTAGDebug ++                         // add SimJTAG if JTAG for debug exposed
//  new chipyard.harness.WithSimDMI ++                               // add SimJTAG if DMI exposed
  new freechips.rocketchip.subsystem.WithJtagDTM ++                 // set the debug module to expose a JTAG port
  new chipyard.harness.WithGPIOTiedOff ++                          // tie-off chiptop GPIOs, if GPIOs are present
  new chipyard.harness.WithSimSPIFlashModel ++                     // add simulated SPI flash memory, if SPI is enabled
  new chipyard.harness.WithSimAXIMMIO ++                           // add SimAXIMem for axi4 mmio port, if enabled
  new chipyard.harness.WithTieOffInterrupts ++                     // tie-off interrupt ports, if present
  new chipyard.harness.WithTieOffL2FBusAXI ++                      // tie-off external AXI4 master, if present
  new chipyard.harness.WithCustomBootPinPlusArg ++                 // drive custom-boot pin with a plusarg, if custom-boot-pin is present
  new chipyard.harness.WithSimUARTToUARTTSI ++                     // connect a SimUART to the UART-TSI port
  new chipyard.harness.WithClockFromHarness ++                     // all Clock I/O in ChipTop should be driven by harnessClockInstantiator
  new chipyard.harness.WithResetFromHarness ++                     // reset controlled by harness
  new chipyard.harness.WithAbsoluteFreqHarnessClockInstantiator ++ // generate clocks in harness with unsynthesizable ClockSourceAtFreqMHz

  // The IOBinders instantiate ChipTop IOs to match desired digital IOs
  // IOCells are generated for "Chip-like" IOs
  new chipyard.iobinders.WithSerialTLIOCells ++
  new chipyard.iobinders.WithDebugIOCells ++
  new chipyard.iobinders.WithUARTIOCells ++
  new chipyard.iobinders.WithGPIOCells ++
  new chipyard.iobinders.WithSPIFlashIOCells ++
  new chipyard.iobinders.WithExtInterruptIOCells ++
  new chipyard.iobinders.WithCustomBootPin ++
  // The "punchthrough" IOBInders below don't generate IOCells, as these interfaces shouldn't really be mapped to ASIC IO
  // Instead, they directly pass through the DigitalTop ports to ports in the ChipTop
  new chipyard.iobinders.WithI2CPunchthrough ++
  new chipyard.iobinders.WithSPIIOPunchthrough ++
  new chipyard.iobinders.WithAXI4MemPunchthrough ++
  new chipyard.iobinders.WithAXI4MMIOPunchthrough ++
  new chipyard.iobinders.WithTLMemPunchthrough ++
  new chipyard.iobinders.WithL2FBusAXI4Punchthrough ++
  new chipyard.iobinders.WithBlockDeviceIOPunchthrough ++
  new chipyard.iobinders.WithNICIOPunchthrough ++
  new chipyard.iobinders.WithTraceIOPunchthrough ++
  new chipyard.iobinders.WithUARTTSIPunchthrough ++
  new chipyard.iobinders.WithNMITiedOff ++

  // By default, punch out IOs to the Harness
  new chipyard.clocking.WithPassthroughClockGenerator ++
  new chipyard.clocking.WithClockGroupsCombinedByName(("uncore", Seq("sbus", "mbus", "pbus", "fbus", "cbus", "obus", "implicit"), Seq("tile"))) ++
  new chipyard.config.WithPeripheryBusFrequency(500.0) ++           // Default 500 MHz pbus
  new chipyard.config.WithMemoryBusFrequency(500.0) ++              // Default 500 MHz mbus
  new chipyard.config.WithControlBusFrequency(500.0) ++             // Default 500 MHz cbus
  new chipyard.config.WithSystemBusFrequency(500.0) ++              // Default 500 MHz sbus
  new chipyard.config.WithFrontBusFrequency(500.0) ++               // Default 500 MHz fbus
  new chipyard.config.WithOffchipBusFrequency(500.0) ++             // Default 500 MHz obus

  new testchipip.boot.WithCustomBootPin ++                          // add a custom-boot-pin to support pin-driven boot address
  new testchipip.boot.WithBootAddrReg ++                            // add a boot-addr-reg for configurable boot address
  new testchipip.serdes.WithSerialTL(Seq(                           // add a serial-tilelink interface
    testchipip.serdes.SerialTLParams(
      client = Some(testchipip.serdes.SerialTLClientParams(idBits=4)), // serial-tilelink interface will master the FBUS, and support 4 idBits
      width = 32                                                    // serial-tilelink interface with 32 lanes
    )
  )) ++
  new chipyard.config.WithDebugModuleAbstractDataWords(8) ++        // increase debug module data capacity
  new chipyard.config.WithBootROM ++                                // use default bootrom
  new chipyard.config.WithUART ++                                   // add a UART
  new chipyard.config.WithL2TLBs(1024) ++                           // use L2 TLBs
  new chipyard.config.WithNoSubsystemDrivenClocks ++                // drive the subsystem diplomatic clocks from ChipTop instead of using implicit clocks
  new chipyard.config.WithInheritBusFrequencyAssignments ++         // Unspecified clocks within a bus will receive the bus frequency if set
  new freechips.rocketchip.subsystem.WithNMemoryChannels(1) ++      // Default 1 memory channels
  new freechips.rocketchip.subsystem.WithClockGateModel ++          // add default EICG_wrapper clock gate model
  new freechips.rocketchip.subsystem.WithNoMMIOPort ++              // no top-level MMIO master port (overrides default set in rocketchip)
  new freechips.rocketchip.subsystem.WithNoSlavePort ++             // no top-level MMIO slave port (overrides default set in rocketchip)
  new freechips.rocketchip.subsystem.WithNExtTopInterrupts(0) ++    // no external interrupts
  new freechips.rocketchip.subsystem.WithDontDriveBusClocksFromSBus ++ // leave the bus clocks undriven by sbus
  new freechips.rocketchip.subsystem.WithCoherentBusTopology ++     // hierarchical buses including sbus/mbus/pbus/fbus/cbus/l2
  new freechips.rocketchip.subsystem.WithDTS("ucb-bar,chipyard", Nil) ++ // custom device name for DTS
  new freechips.rocketchip.system.BaseConfig)
