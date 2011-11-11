#!/usr/bin/perl -w

$toJoin = $ARGV[0];
$numChunks = $ARGV[1];
$outFile = "${toJoin}.csv";

`head -1 ${toJoin}.chunk-000.csv > $outFile`;

$sedCmd = "sed 1,1d ";

for ($i = 0; $i < $numChunks; $i++) {

$chunkString = ".chunk-" . sprintf("%03d", $i);
$inFileName = $toJoin . $chunkString . ".csv";

$cmdString = "$sedCmd $inFileName >> $outFile";
$rmString = "rm $inFileName";
`$cmdString`;
`$rmString`;

#print "cmdString is $cmdString\n";
#print "rmString is $rmString\n";


}
