#!/usr/bin/perl -w

$dir = $ARGV[1];
$numChunks = $ARGV[0];
$SIMTERNET_DIR = $ENV{'SIMTERNET_DIR'};

push @files, "ASPFitness";
push @files, "BackboneInfo";
push @files, "ConsumerData";
push @files, "EdgeData";
push @files, "EdgeMarket";
push @files, "NSP-ASP-Interconnection";
push @files, "NSPFitness";

if ($dir) {
	$old_dir = `pwd`;
	`cd $dir`;
}

while ($file = pop @files) {
	`${SIMTERNET_DIR}data/analysis/csvjoiner.pl $file $numChunks`;
	
}

if ($dir) {
	`cd $old_dir`;
}
