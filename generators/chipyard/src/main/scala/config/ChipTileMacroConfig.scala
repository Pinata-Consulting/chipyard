package chipyard

import org.chipsalliance.cde.config.{Config}
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.subsystem.{MBUS, SBUS}
import testchipip.soc.{OBUS}
import org.chipsalliance.cde.config.Config
import freechips.rocketchip.subsystem._
import freechips.rocketchip.prci.StretchedResetCrossing

// SBUS on different clock from everything else
class MegaBoomMacroConfig extends Config(
  //==================================
  // Set up TestHarness
  //==================================
  new chipyard.harness.WithAbsoluteFreqHarnessClockInstantiator ++ // use absolute frequencies for simulations in the harness
    // NOTE: This only simulates properly in VCS

    //==================================
    // Set up tiles
    //==================================
    new boom.common.WithNMegaBooms(2, crossing=RocketCrossingParams(crossingType=AsynchronousCrossing(),
  resetCrossingType=new StretchedResetCrossing(4)
  )) ++                                                  // 1 Mega Boom
    new boom.common.WithAsynchronousBoomTiles ++

    // Frequency specifications
    new chipyard.config.WithTileFrequency(200.0) ++
    new chipyard.config.WithSystemBusFrequency(200.0) ++
    new chipyard.config.WithMemoryBusFrequency(100.0) ++
    new chipyard.config.WithOffchipBusFrequency(100.0) ++
    new chipyard.config.WithControlBusFrequency(100.0) ++
    new chipyard.config.WithFrontBusFrequency(100.0) ++
    new chipyard.config.WithPeripheryBusFrequency(100.0) ++
    //  Crossing specifications
    new chipyard.config.WithSbusToCbusCrossingType(AsynchronousCrossing()) ++ // Add Async crossing between SBUS and CBUS
    new chipyard.config.WithFbusToSbusCrossingType(AsynchronousCrossing()) ++ // Add Async crossing between FBUS and SBUS
    new chipyard.config.WithSbusToMbusCrossingType(AsynchronousCrossing()) ++ // Add Async crossings between backside of L2 and MBUS
    // Set up I/O
    new testchipip.serdes.WithSerialTLWidth(4) ++                                         // 4bit wide Serialized TL interface to minimize IO
    new testchipip.serdes.WithSerialTLMem(size = (1 << 30) * 4L) ++                       // Configure the off-chip memory accessible over serial-tl as backing memory
    new freechips.rocketchip.subsystem.WithNoMemPort ++                                   // Remove axi4 mem port
    new freechips.rocketchip.subsystem.WithNMemoryChannels(1) ++                          // 1 memory channel

    //==================================
    // Set up buses
    //==================================
    new testchipip.soc.WithOffchipBusClient(MBUS) ++                                      // offchip bus connects to MBUS, since the serial-tl needs to provide backing memory
    new testchipip.soc.WithOffchipBus ++                                                  // attach a offchip bus, since the serial-tl will master some external tilelink memory

    // Create the uncore clock group
    new chipyard.clocking.WithClockGroupsCombinedByName(
      ("uncore", Seq("sbus", "tile", "implicit"), Nil), // clock_uncore
      ("bus", Seq("mbus", "cbus", "fbus", "pbus"), Nil) // clock_bus
    ) ++

    new chipyard.config.AbstractConfig)
