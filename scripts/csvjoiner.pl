#!/usr/bin/perl -w

$cwd = `pwd`;
chomp $cwd;

print "Working in $cwd\n";

# get to the data/per-step directory
$target_dir = "$cwd/data/output/PerStep/";
chdir $target_dir or die "Cannot get to data directory!\n";

# iterate through each of the generations in the output directory
foreach $gen_dir (`ls`) {
	chomp $gen_dir;
	chdir $gen_dir or die "Cannot chdir to ${gen_dir}!\n";
	print "Processing $gen_dir\n";

	#iterate through each file in the output directory
	foreach $file (`ls`) {
		chomp $file;
		
		$class = $file;
		$class =~ s/(.*)\.chunk-.*\.csv/$1/;

		`sed 1d $file >> ../../${class}.csv`;
		`rm $file`;
		
	}
	
	chdir $target_dir;
} 


chdir $cwd;


