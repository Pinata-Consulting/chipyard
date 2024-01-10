rm -rf megaboom/
mkdir -p megaboom
firtool \
	--format=fir \
	--export-module-hierarchy \
	--verify-each=true \
	--warn-on-unprocessed-annotations \
	--disable-annotation-classless \
	--disable-annotation-unknown \
	--mlir-timing \
	--lowering-options=emittedLineLength=2048,noAlwaysComb,disallowLocalVariables,verifLabels,locationInfoStyle=wrapInAtSquareBracket,disallowPackedArrays,omitVersionComment \
	--annotation-file=/home/oyvind/chipyard/vlsi/generated-src/chipyard.harness.TestHarness.ChipLikeMegaBoomConfig/chipyard.harness.TestHarness.ChipLikeMegaBoomConfig.sfc.anno.json \
	--split-verilog \
    --disable-all-randomization \
	--strip-debug-info \
	-o megaboom/ \
	/home/oyvind/chipyard/vlsi/generated-src/chipyard.harness.TestHarness.ChipLikeMegaBoomConfig/chipyard.harness.TestHarness.ChipLikeMegaBoomConfig.sfc.fir
