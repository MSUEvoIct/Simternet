
library(sqldf)


ASPFitness <- read.csv("ASPFitness.csv")
NSPFitness <- read.csv("NSPFitness.csv")
BackboneInfo <- read.csv("BackboneInfo.csv")
ConsumerData <- read.csv("ConsumerData.csv")
EdgeData <- read.csv("EdgeData.csv")
EdgeMarket <- read.csv("EdgeMarket.csv")
NSPASPInterconnection <- read.csv("NSP-ASP-Interconnection.csv")

ASPFitness <- read.csv("ASPFitness.chunk-000.csv")
NSPFitness <- read.csv("NSPFitness.chunk-000.csv")
BackboneInfo <- read.csv("BackboneInfo.chunk-000.csv")
ConsumerData <- read.csv("ConsumerData.chunk-000.csv")
EdgeData <- read.csv("EdgeData.chunk-000.csv")
EdgeMarket <- read.csv("EdgeMarket.chunk-000.csv")
NSPASPInterconnection <- read.csv("NSP-ASP-Interconnection.chunk-000.csv")

generation = max(NSPFitness$Generation)

nsp0 <- NSPFitness[grep('NSP-0',NSPFitness$NSP),]
nsp1 <- NSPFitness[grep('NSP-1',NSPFitness$NSP),]
nsp2 <- NSPFitness[grep('NSP-2',NSPFitness$NSP),]
nsp3 <- NSPFitness[grep('NSP-3',NSPFitness$NSP),]
nsp4 <- NSPFitness[grep('NSP-4',NSPFitness$NSP),]

NSPFitness[Step=0]

gini = c()
timesThrough = 0
for(i in 0:max(NSPFitness$Step)) {
	timesThrough <- timesThrough + 1
	stepData <- NSPFitness[NSPFitness$Step==i,]
	gini <- append(gini,sum((stepData$MarketShare * 100)^2))
}
timesThrough
gini



nsp0edgedata <- EdgeData[grep('NSP-0',EdgeData$NSP),]
avgPrice = c();
timesThrough = 0
for(i in 0:max(nsp0edgedata$Step)) {
	timesThrough <- timesThrough + 1
	stepData <- nsp0edgedata[nsp0edgedata$Step==i,]
	avgPrice <- append(avgPrice,avg(stepData$Price))
	gini <- append(gini,sum((stepData$MarketShare * 100)^2))
}
timesThrough


## Calculate # of NSP Bankruptcies
# get # of generations
numSteps = max(NSPFitness$Step);
lastStep = sqldf(paste("select * from NSPFitness where Step = ",numSteps))
numNSPs = length(lastStep$Bankrupt)
sqldf("select count(bankrupt) from lastStep where Bankrupt == TRUE")

