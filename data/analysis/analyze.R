############################################################
# Simternet Analysis Script (in R)                         #
############################################################



############################################################
# Load required data and modules

## Load Required Libraries
library(hexbin)
library(sqldf)

## Assumes R's working directory is where this script is
## Load data from CSV Files
AspFitness <- read.csv("../output/ApplicationProviderFitness.out.csv")
NspFitness <- read.csv("../output/NetworkProviderFitness.out.csv")

#Interconnection between ASPs and NSPs
AspInterconnection <- read.csv("../output/ASPInterconnection.out.csv")

#ConsumerData <- read.csv("../output/ConsumerData.out.csv")

# EdgeData has one observation per edge network 
EdgeData <- read.csv("../output/EdgeData.out.csv")
# EdgeMarket has one observation for each X,Y grid coordinate
EdgeMarket <- read.csv("../output/EdgeMarket.out.csv")

# Variables useful for all sections
numGenerations <- max(NspFitness$Generation)
graphHeight <- 800
graphWidth <- 800
# separate standard width for graphs with generation on X axis
genGraphWidth <- graphWidth
graphMinGenerationWidth <- 10

# A large part of the analysis below, analyzing relationships, does so
# over subsets of generations; looking at the results overall would be 
# biased toward behavior that has been competed away.  This allows us
# to make inferences based on agents which have acquired some intelligence
# numGenerationSplits creates X different graphs, e.g., for 100 generations,
# one graph for gens 1-25, 26-49, 50-74, 75-100)
numGenerationSplits <- 4

if ( (graphMinGenerationWidth * numGenerations) > graphWidth) {
  genGraphWidth <- graphMinGenerationWidth * numGenerations;
}


##############################################################
# Analyze Evolutionary History (x axis is Generation)        #
##############################################################

## Compute Aggregate Statistics
EdgeDataAverages <- aggregate(EdgeData, by = list(Gen = EdgeData$Generation), FUN = mean)
EdgeMarketAverages <- aggregate(EdgeMarket, by = list(Gen = EdgeMarket$Generation), FUN = mean)
EdgeDataTotalCustomers <- aggregate(EdgeData$Customers, by = list(Gen = EdgeData$Generation), FUN = sum)


####### Finess Scores ####### 
# Network Service Providers
png("graphs/history/NetworkProvider.Fitness.png", width=genGraphWidth, height=graphHeight)
boxplot(log(Fitness + 1) ~ Generation, data=NspFitness,
    xlab="Generation", ylab="Fitness (log)", main="NSP Fitness")
dev.off()

# Application Service Providers
png("graphs/history/ApplicationProvider.Fitness.png", width=genGraphWidth, height=graphHeight)
boxplot(log(Fitness + 1) ~ Generation, data=AspFitness,
    xlab="Generation", ylab="Fitness (log)", main="ASP Fitness")
dev.off()


######## General Market Informaiton ########
# Total # of Customers
png("graphs/history/NetworkProvider.TotalCustomers.png", width=genGraphWidth, height=graphHeight)
plot(x ~ Gen, data = EdgeDataTotalCustomers, xlab = "Generation", ylab="Total Customers", main = "Total # of Consumer Subs")
dev.off()


######## Network Provider Detail Histories #######

### Market Results ###
# Overall Market Share
png("graphs/history/NetworkProvider.MarketShare.png", width=genGraphWidth, height=graphHeight)
boxplot(MarketShare ~ Generation, data=NspFitness, main = "NSP Marketshare", 
ylab="Share of Total Potential Market (all locations)", xlab="Generation")
dev.off()

# Edge Market Share
png("graphs/history/NetworkProvider.EdgeMarketShare.Average.png", 
   width=genGraphWidth, 
   height=graphHeight)
boxplot(MarketShare ~ Generation, data = EdgeData, 
    xlab = "Generation",
    ylab = "Local Market Share (per edge network)",
    main = "Network Provider Local Market Shares")
dev.off()

# Edge Prices
png("graphs/history/NetworkProvider.EdgePrice.png", width=genGraphWidth, height=graphHeight)
boxplot(Price ~ Generation, data = EdgeData, main = "Edge Network Prices")
dev.off()

### Investment ###
# Total Investment
png("graphs/history/NetworkProvider.Investment.png", width=genGraphWidth, height=graphHeight)
boxplot(log(TotalInvestment + 1) ~ Generation, data=NspFitness,
    xlab="Generation", ylab="Total Investment (log)", main="NSP Investment")
dev.off()

### Edge Network Details ###
# Number of Edges per NSP (boxplot)
png("graphs/history/NetworkProvider.EdgeCount.png", width=genGraphWidth, height=graphHeight)
boxplot(NumEdges ~ Generation, 
  data = NspFitness,
  main = "NSP Total # of Edge Networks", 
  ylab="# of Edge Networks",
  xlab="Generation")
dev.off()

# Average transit bandwidth of Edge Network
png("graphs/history/EdgeNetworks-TransitBandwidth.png", width=genGraphWidth, height=graphHeight)
boxplot(log(TransitBandwidth+1) ~ Generation, data = EdgeData, 
   xlab = "Generation", ylab = "Transit Bandwidth",
   main = "Transit Bandwidth of Edge Networks")
dev.off()

# Number of Edges per NSP (plot of average)
png("graphs/history/NetworkProvider.EdgeCountAverage.png", width=genGraphWidth, height=graphHeight)
sqlString <- "select avg(NumEdges) as NumEdges, Generation from NspFitness group by Generation"
tmp <- sqldf(sqlString)
plot(NumEdges ~ Generation, 
  data = tmp,
  main = "Average Number of Edge Networks per NSP", 
  ylab="Average # of Edge Networks",
  xlab="Generation")
rm(tmp)
dev.off()

# Congestion of Edge Networks
png("graphs/history/EdgeNetworks.Congestion.png", width=genGraphWidth, height=graphHeight)
boxplot(Congestion ~ Generation, data = EdgeData, main = "Congestion of Edge Networks",
   xlab="Generation", ylab="Usage Demanded/Capacity")
dev.off()


######## Application Provider Details ########
# Number of Customers
png("graphs/history/ApplicationProvider.Customers.png", width=genGraphWidth, height=graphHeight)
boxplot(log(NumCustomers+1) ~ Generation, data=AspFitness,
    xlab="Generation", ylab="# of Customers (log)", main = "Customers per Application Provider")
dev.off()

# Quality
png("graphs/history/ApplicationProvider.Quality.png", width=genGraphWidth, height=graphHeight)
boxplot(Quality ~ Generation, data=AspFitness,
    xlab="Generation", ylab="Quality", main = "ASP Quality")
dev.off()

# Investment
png("graphs/history/ApplicationProvider.Investment.png", width=genGraphWidth, height=graphHeight)
boxplot(log(TotalInvestment) ~ Generation, data=AspFitness,
    xlab="Generation", ylab="log(Total Investment)", main="Investment per Application Provider")
dev.off()




## NSP/ASP Interconnection Data
# Prices by Generation
png("graphs/history/InterconnectionPrice-boxplot.png", width=genGraphWidth, height=graphHeight)
boxplot(log10(Price) ~ Generation, data=AspInterconnection,
    xlab="Generation", ylab="Price (log 10)", main="Price for NSP Interconnection")
dev.off()



## Clean Up
rm(EdgeDataAverages)
rm(EdgeMarketAverages)
rm(EdgeDataTotalCustomers)



#####################################################################
# Relationships Section, Split into numGenerationSplits sections    #
#####################################################################

genWidth <- (numGenerations + 1) / numGenerationSplits
curGen <- 0
curGroup <- 0

buildFN <- function(description,num) {
  filename <- paste("graphs/relationships/", description, ".", num, ".png", sep="")
  return(filename)
}

subsetFN <- function(frame,curGen,maxGen) {
  sqlStatement <- paste("select * from", frame, "where Generation >=", curGen, "and Generation <=", maxGen)
  return(sqldf(sqlStatement))
}

###### GENERATION SPLITTING WHILE LOOP #####
while (curGen < numGenerations) {
maxGen <- curGen + genWidth - 1;
curGen
maxGen

# Subset of generations
NspFitnessTemp <- subsetFN("NspFitness", curGen, maxGen)
AspFitnessTemp <- subsetFN("AspFitness", curGen, maxGen)
AspInterconnectionTemp <- subsetFN("AspInterconnection", curGen, maxGen)
#ConsumerDataTemp <- subsetFN("ConsumerData", curGen, maxGen)
EdgeDataTemp <- subsetFN("EdgeData", curGen, maxGen)
EdgeMarketTemp <- subsetFN("EdgeMarket", curGen, maxGen)

# convient descriptor for graphs
genDescriptor <- paste("Generations", curGen, "through", maxGen)

###### Detail Graphs ######

# NSP Fitness(TotalInvestment)
png(buildFN("NetworkProvider.Fitness-Investment", curGroup), width=graphWidth, height=graphHeight)
bin <- hexbin(log(NspFitnessTemp$TotalInvestment+1), log(NspFitnessTemp$Fitness+1), xbins=50)
plot(bin, 
  main= paste("NSP Fitness v. Investment for", genDescriptor), 
  xlab="Investment (log)", ylab="Fitness (log)")
dev.off()
rm(bin)

# NSP Fitness(NumCustomers)
png(buildFN("NetworkProvider.Fitness-Customers", curGroup), width=graphWidth, height=graphHeight)
bin <- hexbin(NspFitnessTemp$NumCustomers, log(NspFitnessTemp$Fitness+1), xbins=50)
plot(bin, 
    main= paste("NSP Fitness v. Total # Customers", genDescriptor), 
    xlab="Total Customers", ylab="Fitness (log)")
dev.off()
rm(bin)

# NSP Fitness(NumEdges)
png(buildFN("NetworkProvider.Fitness-NumEdges", curGroup), width=graphWidth, height=graphHeight)
bin <- hexbin(NspFitnessTemp$NumEdges, log(NspFitnessTemp$Fitness+1), xbins=50)
plot(bin, main= paste("NSP Fitness v. # of Edge Networks\n", genDescriptor), 
xlab="Edge Networks", ylab="Fitness (log)")
dev.off()
rm(bin)


# NSP Edge Market Share(Price)
png(buildFN("NetworkProvider.Edge.MarketShare-Price", curGroup), width=graphWidth, height=graphHeight)
bin <- hexbin(EdgeDataTemp$MarketShare, EdgeDataTemp$Price, xbins=50)
plot(bin, 
    main= paste("Edge Market Share v. Price", genDescriptor), 
    xlab="Market Share", ylab="Price")
dev.off()
rm(bin)

# NSP Edge Price(#Competitors)
png(buildFN("NetworkProvider.Edge.Price-Competitors", curGroup), width=graphWidth, height=graphHeight)
boxplot(Price ~ Competitors, data = EdgeDataTemp,
   xlab = "Competitors",
   ylab = "Price",
   main = paste("Edge Network Prices v. Competitors\n", genDescriptor) )
dev.off()


# NSP Average Edge Price v. Total Market Share
sqlString = paste("select NspFitnessTemp.Generation, NspFitnessTemp.Chunk, NspFitnessTemp.NSP, NspFitnessTemp.Fitness, ",
   "NspFitnessTemp.MarketShare, NspFitnessTemp.Bankrupt, avg(EdgeDataTemp.Price) as AveragePrice ", 
   "from NspFitnessTemp left join EdgeDataTemp on EdgeDataTemp.Generation = NspFitnessTemp.Generation", 
   "and EdgeDataTemp.Chunk = NspFitnessTemp.Chunk and EdgeDataTemp.NSP = NspFitnessTemp.NSP ",
   "where NspFitnessTemp.Bankrupt = 'false'", 
   "group by NspFitnessTemp.Generation, NspFitnessTemp.Chunk, NspFitnessTemp.NSP, ", 
   " NspFitnessTemp.Fitness, NspFitnessTemp.MarketShare",
   "order by NspFitnessTemp.Generation, NspFitnessTemp.Chunk, NspFitnessTemp.NSP")
tmp <- sqldf(sqlString)
rm(sqlString)
bin <- hexbin(tmp$MarketShare, as.numeric(tmp$AveragePrice), xbins=50)
png(buildFN("NetworkProvider.Edge.AveragePrice-MarketShare", curGroup), width=graphWidth, height=graphHeight)
plot(bin, 
    main= paste("Average Edge Price v. NSP Total Market Share\n", genDescriptor), 
    xlab="Market Share (All Locations)", ylab="Average Price of Edge Networks")
dev.off()
rm(tmp)

# Networks per grid cell
png(buildFN("EdgeNetworkDensity", curGroup), width=graphWidth, height=graphHeight)
hist(EdgeMarketTemp$NumNetworks, main = paste("Edge Network Density\n", genDescriptor), 
  xlab="# of Edges at Location", breaks=5)
dev.off()


######## ASP/NSP Interconnection ########
# Quantity(Price)
png(buildFN("ASP-NSP-Interconnection.Market", curGroup), width=graphWidth, height=graphHeight)
bin <- hexbin(log(AspInterconnectionTemp$Price), log(AspInterconnectionTemp$Quantity + 1), xbins=50)
plot(bin, main=paste("ASP/NSP Interconnection Market\n", genDescriptor), 
  xlab="Bandwidth Price (log)", ylab="Qty Purchased (log)")
dev.off()
rm(bin)


########### Application Service Provider Information ##############

# Fitness(Investment)
png(buildFN("ApplicationProvider.Fitness-Investment", curGroup), width=graphWidth, height=graphHeight)
bin <- hexbin(AspFitnessTemp$TotalInvestment, AspFitnessTemp$Fitness, xbins=50)
plot(bin, main= paste("ASP Fitness v. Investment", genDescriptor),
  xlab="Total Investment", ylab="")
dev.off()
rm(bin)

# Fitness(Quality)
png(buildFN("ApplicationProvider.Fitness-Quality", curGroup), width=graphWidth, height=graphHeight)
bin <- hexbin(AspFitnessTemp$Quality, AspFitnessTemp$Fitness, xbins=50)
plot(bin, main= paste("ASP Fitness v. Quality\n", genDescriptor), 
  xlab="ASP Quality", ylab="ASP Fitnes")
dev.off()
rm(bin)

# ASP Fitness(#Customers)
png(buildFN("ApplicationProvider.Fitness-Customers", curGroup), width=graphWidth, height=graphHeight)
bin <- hexbin(AspFitnessTemp$NumCustomers, AspFitnessTemp$Fitness, xbins=50)
plot(bin, main= paste("ASP Fitness v. # of Customers\n", genDescriptor), 
xlab="# of Customers", ylab="ASP Fitness")
dev.off()
rm(bin)




### Loop Stuff ###
rm(genDescriptor)
rm(NspFitnessTemp)
rm(AspFitnessTemp)
rm(AspInterconnectionTemp)
#rm(ConsumerDataTemp)
rm(EdgeDataTemp)
rm(EdgeMarketTemp)
curGroup <- curGroup + 1
curGen <- curGen + genWidth

###### END GENERATION SPLITTING WHILE LOOP #####
}



