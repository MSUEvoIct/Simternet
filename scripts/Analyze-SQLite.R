##
#  This script expects to run in data/output.
##

## Necessary Libraries
library(lattice)
library(sqldf)

## General Parameters
db <- "simternet.db"  # specify SQLite DB File
graphHeight <- 800
graphWidth <- 800   
graphHistoryDir <- "../graphs/history/" # Evolutionary History
graphRelDir <- "../graphs/relationships/" # suspected relationships


## Obtain Simulation information from database
numGenerations = sqldf("SELECT max(Generation) from NSPFitness", dbname=db)
numSteps = sqldf("SELECT max(Step) from NSPFitness", dbname=db)

numASPs = sqldf("SELECT count(*) from ASPFitness WHERE Generation = 0 AND Step = 0 AND Chunk = 0", dbname=db)
numNSPs = sqldf("SELECT count(*) from NSPFitness WHERE Generation = 0 AND Step = 0 AND Chunk = 0", dbname=db)
numChunks = sqldf("SELECT max(Chunk) from NSPFitness", dbname=db)
# consumerTotalPopulation = sqldf("select sum(Population) as TotalPopulation from ConsumerData where Generation = 0 and Step = 0", dbname=db)
totalASPAgents = (numASPs) * (numChunks + 1)
totalNSPAgents = (numNSPs) * (numChunks + 1)

##
# Fitnesses
##

### ASP Fitness

# Evolutionary Time
Query = paste(
"SELECT ",
"Generation, ", 
"min(Fitness) as Minimum, ",
"avg(Fitness) as Mean, ",
"max(Fitness) as Max ",
"FROM ASPFitness ",
"WHERE Fitness != 'NaN' ",
"GROUP BY Generation ",
"ORDER BY Generation"
)
ASPEvolutionaryTimeFitness = sqldf(Query, dbname=db)

# Evolutionary plus Simulation Time
Query = paste(
"SELECT ",
"Generation, ", 
"Step, ", 
"min(Fitness) as Minimum, ",
"avg(Fitness) as Mean, ",
"max(Fitness) as Max ",
"FROM ASPFitness ",
"WHERE Fitness != 'NaN' ",
"GROUP BY Generation, Step ",
"ORDER BY Generation, Step "
)
ASPEvolutionaryAndSimulationTimeFitness = sqldf(Query, dbname=db)
attach(ASPEvolutionaryAndSimulationTimeFitness)

# Graph of Mean
png(filename=paste(graphHistoryDir,"ASPMeanFitness.png"), 
	height=graphHeight, 
	width=graphWidth, 
	units = "px")
levelplot(Mean~Generation*Step, 
	cuts=100, col.regions = terrain.colors(200),
	pretty=TRUE, 
	main="Mean ASP Fitness by Generation and Step")
dev.off()

# Graph of Max
png(filename=paste(graphHistoryDir,"ASPMaxFitness.png"), 
	height=graphHeight, 
	width=graphWidth, 
	units = "px")
levelplot(Max~Generation*Step, 
	cuts=100, col.regions = terrain.colors(200),
	pretty=TRUE, 
	main="Max ASP Fitness by Generation and Step")
dev.off()


### NSP Fitness
# Evolutionary Time


# Evolutionary and Simulation Time
Query = paste(
"SELECT ",
"Generation, ", 
"Step, ", 
"min(Fitness) as MinimumFitness, ",
"avg(Fitness) as MeanFitness, ",
"max(Fitness) as MaxFitness ",
#"sum((MarketShare*100)*(MarketShare*100)) as TotalMarketHHI ",
"FROM NSPFitness ",
"WHERE Fitness != 'NaN' ",
"GROUP BY Generation, Step ",
"ORDER BY Generation, Step "
)
NSPEvolutionaryAndSimulationTime = sqldf(Query, dbname=db)

# Graph of Mean Fitness
png(filename=paste(graphHistoryDir,"NSPMeanFitness.png"), 
	height=graphHeight, 
	width=graphWidth, 
	units = "px")
levelplot(MeanFitness^0.5~Generation*Step, 
	cuts=100, col.regions = terrain.colors(200),
	data=NSPEvolutionaryAndSimulationTime,
	pretty=TRUE, 
	main="Mean NSP Fitness (^0.5) by Generation and Step")
dev.off()

# Graph of Max Fitness
png(filename=paste(graphHistoryDir,"NSPMaxFitness.png"), 
	height=graphHeight, 
	width=graphWidth, 
	units = "px")
levelplot(MaxFitness^0.5~Generation*Step, 
	data=NSPEvolutionaryAndSimulationTime,
	cuts=100, col.regions = terrain.colors(200),
	pretty=TRUE, 
	main="Max NSP (^0.5) Fitness by Generation and Step")
dev.off()

# Total Market HHI
# need to get total market data
png(filename=paste(graphHistoryDir,"NSPTotalMarketHHI.png"), 
	height=graphHeight, 
	width=graphWidth, 
	units = "px")
levelplot(TotalMarketHHI~Generation*Step, 
	cuts=100, col.regions = terrain.colors(200),
	data=NSPEvolutionaryAndSimulationTime,
	pretty=TRUE, 
	main="HHI For National Market by Generation and Step")
dev.off()




## Consumer Data
# By Evolutionary and Simulation Time
Query <- paste(
"SELECT ",
"Generation, ",
"Step, ",
"sum(Population) as TotalPopulation, ",
"avg(PaidToNSPs) as MeanPaidToNSPs, ",
"max(PaidToNSPs) as MaxPaidToNSPs, ",
"avg(BenefitReceived) as MeanBenefitReceived, ",
"max(BenefitReceived) as MaxBenefitReceived, ",
"avg(TransferRequested) as MeanTransferRequested, ",
"avg(TransferReceived) as MeanTransferReceived ",
"FROM ConsumerData ",
"GROUP BY Generation, Step ",
"ORDER BY Generation, Step"
)
#ConsumerEvoAndSimTime = sqldf(Query, dbname=db)

# Population (should be mostly invariant)
#png(filename=paste(graphHistoryDir,"ConsumerPopulation.png"), 
#	height=graphHeight, 
#	width=graphWidth, 
#	units = "px")
#levelplot(TotalPopulation~Generation*Step, 
#	cuts=100, col.regions = terrain.colors(200),
#	data= ConsumerEvoAndSimTime,
#	pretty=TRUE, 
#	main="Consumer Population by Generation and Step")
#dev.off()

# Mean Consumer Payments to NSPs by Generation and Step
#png(filename=paste(graphHistoryDir,"ConsumerMeanPaymentsToNSPs.png"), 
#	height=graphHeight, 
#	width=graphWidth, 
#	units = "px")
#levelplot(MeanPaidToNSPs~Generation*Step, 
#	cuts=100, col.regions = terrain.colors(200),
#	data= ConsumerEvoAndSimTime,
#	pretty=TRUE, 
#	main="Mean Consumer Payments to NSPs by Generation and Step")
#dev.off()

# Max Consumer Payments to NSPs by Generation and Step
#png(filename=paste(graphHistoryDir,"ConsumerMaxPaymentsToNSPs.png"), 
#	height=graphHeight, 
#	width=graphWidth, 
#	units = "px")
#levelplot(MaxPaidToNSPs~Generation*Step, 
#	cuts=100, col.regions = terrain.colors(200),
#	data= ConsumerEvoAndSimTime,
#	pretty=TRUE, 
#	main="Max Consumer Payments to NSPs by Generation and Step")
#dev.off()

# Mean Consumer Benefit Received by Generation and Step
#png(filename=paste(graphHistoryDir,"ConsumerMeanBenefitReceived.png"), 
#	height=graphHeight, 
#	width=graphWidth, 
#	units = "px")
#levelplot(MeanBenefitReceived~Generation*Step, 
#	cuts=100, col.regions = terrain.colors(200),
#	data= ConsumerEvoAndSimTime,
#	pretty=TRUE, 
#	main="Mean Consumer Benefits Received by Generation and Step")
#dev.off()

# Max Consumer Benefit Received by Generation and Step
#png(filename=paste(graphHistoryDir,"ConsumerMaxBenefitReceived.png"), 
#	height=graphHeight, 
#	width=graphWidth, 
#	units = "px")
#levelplot(MaxBenefitReceived~Generation*Step, 
#	cuts=100, col.regions = terrain.colors(200),
#	data= ConsumerEvoAndSimTime,
#	pretty=TRUE, 
#	main="Max Consumer Benefit Received by Generation and Step")
#dev.off()

# Consumer Mean Transfer Requested by Generation and Step
#png(filename=paste(graphHistoryDir,"ConsumerMeanTransferRequested.png"), 
#	height=graphHeight, 
#	width=graphWidth, 
#	units = "px")
#levelplot(MeanTransferRequested~Generation*Step, 
#	cuts=100, col.regions = terrain.colors(200),
#	data= ConsumerEvoAndSimTime,
#	pretty=TRUE, 
#	main="Consumer Mean Transfer Requested by Generation and Step")
#dev.off()

# Consumer Mean Transfer Received by Generation and Step
#png(filename=paste(graphHistoryDir,"ConsumerMeanTransferReceived.png"), 
#	height=graphHeight, 
#	width=graphWidth, 
#	units = "px")
#levelplot(MeanTransferReceived~Generation*Step, 
#	cuts=100, col.regions = terrain.colors(200),
#	data= ConsumerEvoAndSimTime,
#	pretty=TRUE, 
#	main="Consumer Mean Transfer Received by Generation and Step")
#dev.off()

# Consumer Mean Congestion by Generation and Step
#png(filename=paste(graphHistoryDir,"ConsumerMeanCongestion.png"), 
#	height=graphHeight, 
#	width=graphWidth, 
#	units = "px")
#levelplot((1-(MeanTransferReceived/MeanTransferRequested))~Generation*Step, 
#	cuts=100, col.regions = terrain.colors(200),
#	data= ConsumerEvoAndSimTime,
#	pretty=TRUE, 
#	main="Consumer Mean Congestion by Generation and Step")
#dev.off()


###
#  Interconnection Data
###
Query <- paste(
"SELECT ",
"Generation, ",
"Step, ",
"avg(Price) as MeanPrice, ",
"max(Price) as MaxPrice, ",
"avg(Quantity) as MeanQuantity, ",
"max(Quantity) as MaxQuantity ",
"FROM NSPASPInterconnection ",
"GROUP BY Generation, Step ",
"ORDER BY Generation, Step"
)
NSPASPInterconnectionEvoAndSimTime = sqldf(Query, dbname=db)

# Mean Price for ASP Interconnection by Generation and Step
png(filename=paste(graphHistoryDir,"NSPASPInterconnectionMeanPrice.png"), 
	height=graphHeight, 
	width=graphWidth, 
	units = "px")
levelplot(MeanPrice~Generation*Step, 
	cuts=100, col.regions = terrain.colors(200),
	data= NSPASPInterconnectionEvoAndSimTime,
	pretty=TRUE, 
	main="Mean Price for ASP Interconnection by Generation and Step")
dev.off()

# Max Price for ASP Interconnection by Generation and Step
png(filename=paste(graphHistoryDir,"NSPASPInterconnectionMaxPrice.png"), 
	height=graphHeight, 
	width=graphWidth, 
	units = "px")
levelplot(MaxPrice~Generation*Step, 
	cuts=100, col.regions = terrain.colors(200),
	data= NSPASPInterconnectionEvoAndSimTime,
	pretty=TRUE, 
	main="Max Price for ASP Interconnection by Generation and Step")
dev.off()

# Mean Quantity for ASP Interconnection by Generation and Step
png(filename=paste(graphHistoryDir,"NSPASPInterconnectionMeanQuantity.png"), 
	height=graphHeight, 
	width=graphWidth, 
	units = "px")
levelplot(MeanQuantity~Generation*Step, 
	cuts=100, col.regions = terrain.colors(200),
	data= NSPASPInterconnectionEvoAndSimTime,
	pretty=TRUE, 
	main="Mean Quantity for ASP Interconnection by Generation and Step")
dev.off()

# Max Quantity for ASP Interconnection by Generation and Step
png(filename=paste(graphHistoryDir,"NSPASPInterconnectionMaxQuantity.png"), 
	height=graphHeight, 
	width=graphWidth, 
	units = "px")
levelplot(MaxQuantity~Generation*Step, 
	cuts=100, col.regions = terrain.colors(200),
	data= NSPASPInterconnectionEvoAndSimTime,
	pretty=TRUE, 
	main="Max Quantity for ASP Interconnection by Generation and Step")
dev.off()


###
# Edge Market Info
###
Query <- paste(
"SELECT ",
"Generation, ", 
"Step, ",
"min(NumNetworks) as MinNumEdges, ",
"avg(NumNetworks) as MeanNumEdges, ",
"max(NumNetworks) as MaxNumEdges",
"FROM EdgeMarket ",
"GROUP BY Generation, Step ",
"ORDER BY Generation, Step"
)
MeanNumEdges <- sqldf(Query, dbname=db)

# Mean # of Edge Networks by Generation and Step
png(filename=paste(graphHistoryDir,"NumEdgesMin.png"), 
	height=graphHeight, 
	width=graphWidth, 
	units = "px")
levelplot(MinNumEdges~Generation*Step, 
	cuts=100, col.regions = terrain.colors(200),
	data= MeanNumEdges,
	pretty=TRUE, 
	main="Min # of Edge Networks by Generation and Step")
dev.off()

# Mean # of Edge Networks by Generation and Step
png(filename=paste(graphHistoryDir,"NumEdgesMean.png"), 
	height=graphHeight, 
	width=graphWidth, 
	units = "px")
levelplot(MeanNumEdges~Generation*Step, 
	cuts=100, col.regions = terrain.colors(200),
	data= MeanNumEdges,
	pretty=TRUE, 
	main="Mean # of Edge Networks by Generation and Step")
dev.off()

# Max # of Edge Networks by Generation and Step
png(filename=paste(graphHistoryDir,"NumEdgesMax.png"), 
	height=graphHeight, 
	width=graphWidth, 
	units = "px")
levelplot(MaxNumEdges~Generation*Step, 
	cuts=100, col.regions = terrain.colors(200),
	data= MeanNumEdges,
	pretty=TRUE, 
	main="Max # of Edge Networks by Generation and Step")
dev.off()


###
# Edge Data by Generation and Step
###
Query <- paste(
"SELECT ",
"Generation, ", 
"Step, ",
"min(Price) as MinEdgePrice, ",
"avg(Price) as MeanEdgePrice, ",
"max(Price) as MaxEdgePrice, ",
"min(TransitBandwidth) as MinTransitBandwidth, ",
"avg(TransitBandwidth) as MeanTransitBandwidth, ",
"max(TransitBandwidth) as MaxTransitBandwidth, ",
"min(Congestion) as MinCongestion, ",
"avg(Congestion) as MeanCongestion, ",
"max(Congestion) as MaxCongestion, ",
"min(Competitors) as MinNumCompetitors, ",
"avg(Competitors) as MeanNumCompetitors, ",
"max(Competitors) as MaxNumCompetitors ",
"FROM EdgeData ",
"WHERE Price < 100 ",
"GROUP BY Generation, Step ",
"ORDER BY Generation, Step"
)
EdgeDataByEvoAndSimTime <- sqldf(Query, dbname=db)

# Min Edge Price by Generation and Step
png(filename=paste(graphHistoryDir,"EdgePriceMin.png"), 
	height=graphHeight, 
	width=graphWidth, 
	units = "px")
levelplot(MinEdgePrice~Generation*Step, 
	cuts=100, col.regions = terrain.colors(200),
	data= EdgeDataByEvoAndSimTime,
	pretty=TRUE, 
	main="Min Edge Price by Generation and Step")
dev.off()

# Mean Edge Price by Generation and Step
png(filename=paste(graphHistoryDir,"EdgePriceMean.png"), 
	height=graphHeight, 
	width=graphWidth, 
	units = "px")
levelplot(MeanEdgePrice~Generation*Step, 
	cuts=100, col.regions = terrain.colors(200),
	data= EdgeDataByEvoAndSimTime,
	pretty=TRUE, 
	main="Mean Edge Price by Generation and Step")
dev.off()

# Max Edge Price by Generation and Step
png(filename=paste(graphHistoryDir,"EdgePriceMax.png"), 
	height=graphHeight, 
	width=graphWidth, 
	units = "px")
levelplot(MaxEdgePrice~Generation*Step, 
	cuts=100, col.regions = terrain.colors(200),
	data= EdgeDataByEvoAndSimTime,
	pretty=TRUE, 
	main="Max Edge Price by Generation and Step")
dev.off()





