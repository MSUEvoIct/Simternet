# Assumes R's working directory is where this script is

## Load data from CSV Files
AspFitness <- read.csv("../output/ApplicationProviderFitness.out.csv")
AspInterconnection <- read.csv("../output/ASPInterconnection.out.csv")
#ConsumerData <- read.csv("../output/ConsumerData.out.csv")
EdgeData <- read.csv("../output/EdgeData.out.csv")
EdgeDataAverages <- aggregate(EdgeData, by = list(Gen = EdgeData$Generation), FUN = mean)
EdgeDataTotalCustomers <- aggregate(EdgeData$Customers, by = list(Gen = EdgeData$Generation), FUN = sum)
NspFitness <- read.csv("../output/NetworkProviderFitness.out.csv")
EdgeMarket <- read.csv("../output/EdgeMarket.out.csv")
EdgeMarketAverages <- aggregate(EdgeMarket, by = list(Gen = EdgeMarket$Generation), FUN = mean)

## Load Required Libraries
library(hexbin)


## Network Service Provider Information
png("graphs/NspFitness-boxplot.png", width=800, height=800)
boxplot(Fitness ~ Generation, data=NspFitness,
    xlab="Generation", ylab="Fitness", main="NSP Fitness")
dev.off()
png("graphs/NspFitness-boxplot-log.png", width=800, height=800)
boxplot(log(Fitness) ~ Generation, data=NspFitness,
    xlab="Generation", ylab="log(fitness)", main="NSP Fitness")
dev.off()

png("graphs/NspCapitalAssets-boxplot.png", width=800, height=800)
boxplot(CapitalAssets ~ Generation, data=NspFitness,
    xlab="Generation", ylab="Capital Assets", main="NSP Capital Assets")
dev.off()
png("graphs/NspCapitalAssets-boxplot-log.png", width=800, height=800)
boxplot(log(CapitalAssets) ~ Generation, data=NspFitness,
    xlab="Generation", ylab="log(Capital Assets)", main="NSP Capital Assets")
dev.off()

png("graphs/NspInvestment-boxplot.png", width=800, height=800)
boxplot(TotalInvestment~ Generation, data=NspFitness,
    xlab="Generation", ylab="Total Investment", main="NSP Investment")
dev.off()
png("graphs/NspInvestment-boxplot-log.png", width=800, height=800)
boxplot(log(TotalInvestment) ~ Generation, data=NspFitness,
    xlab="Generation", ylab="log(Total Investment)", main="NSP Investment")
dev.off()

png("graphs/NspNumEdges-histogram.png", width=800, height=800)
hist(NspFitness$NumEdges, main = "NSP # of Edge Networks", 
xlab="# of Edge Networks", breaks=20)
dev.off()

png("graphs/NspMarketshare-histogram.png", width=800, height=800)
hist(NspFitness$MarketShare, main = "NSP Marketshare", 
xlab="Share of Total Market (all locations)")
dev.off()

# NSP Fitness(TotalInvestment)
png("graphs/NspFitnessInvestment-hexbin.png", width=800, height=800)
bin <- hexbin(NspFitness$TotalInvestment, NspFitness$Fitness^(1/3), xbins=50)
plot(bin, main= "NSP Fitness v. Investment", xlab="Investment", ylab="Fitness^(1/3)")
dev.off()
rm(bin)

# NSP Fitness(NumCustomers)
png("graphs/NspFitnessCustomers-hexbin.png", width=800, height=800)
bin <- hexbin(NspFitness$NumCustomers, NspFitness$Fitness^(1/3), xbins=50)
plot(bin, main= "NSP Fitness v. Total # Customers", 
xlab="Total Customers", ylab="Fitness^(1/3)", main="NSP Fitness v. Customers")
dev.off()
rm(bin)

# NSP Fitness(NumEdges)
png("graphs/NspFitnessNumEdges-hexbin.png", width=800, height=800)
bin <- hexbin(NspFitness$NumEdges, NspFitness$Fitness^(1/3), xbins=50)
plot(bin, main= "NSP Fitness v. # of Edge Networks", 
xlab="Edge Networks", ylab="Fitness^(1/3)")
dev.off()
rm(bin)


## Edge Network Market Data
# Networks per grid cell
png("graphs/EdgeNetworks-density-histogram.png", width=800, height=800)
hist(EdgeMarket$NumNetworks, main = "Edge Network Density", xlab="# of Edges at Location",breaks=5)
dev.off()

png("graphs/EdgeNetworks-density-average.png", width=800, height=800)
plot(NumNetworks ~ Generation, data = EdgeMarketAverages, main = "Average Density of Edges", ylab = "# of Networks at a Location")
dev.off()

# Average # of competitors
png("graphs/EdgeNetworks-competitors-average.png", width=800, height=800)
plot(Competitors ~ Generation, data=EdgeDataAverages, main = "Average # of Edge Network Competitors")
dev.off()

# Average Edge's Market Share
png("graphs/EdgeNetworks-marketshare-average.png", width=800, height=800)
plot(MarketShare ~ Generation, data = EdgeDataAverages, main = "Average Edge's Market Share")
dev.off()

# Average Edge's Price
png("graphs/EdgeNetworks-price-average.png", width=800, height=800)
plot(Price ~ Generation, data = EdgeDataAverages, main = "Average Edge's Price")
dev.off()

# Total # of Customers
png("graphs/EdgeNetworks-customers-total.png", width=800, height=800)
plot(x ~ Gen, data = EdgeDataTotalCustomers, xlab = "Generation", ylab="Total Customers", main = "Total # of Consumer Subs")
dev.off()

# Average customers per Edge Network
png("graphs/EdgeNetworks-subscription-average.png", width=800, height=800)
plot(Customers ~ Generation, data = EdgeDataAverages, main = "Average Customers per Edge")
dev.off()

## Edge Network Operation Data
# Average congestion of Edge Networks
png("graphs/EdgeNetworks-congestion-average.png", width=800, height=800)
plot(Congestion ~ Generation, data = EdgeDataAverages, main = "Average Congestion of Edges")
dev.off()

# Average transit bandwidth of Edge Network
png("graphs/EdgeNetworks-transitbandwidth-average.png", width=800, height=800)
plot(TransitBandwidth ~ Generation, data = EdgeDataAverages, main = "Average Edge's Transit Bandwidth")
dev.off()

# Average customers per Edge Network
png("graphs/EdgeNetworks-subscription-average.png", width=800, height=800)
plot(Customers ~ Generation, data = EdgeDataAverages, main = "Average Customers per Edge")
dev.off()



## Application Provider Information
png("graphs/AspFitness-boxplot.png", width=800, height=800)
boxplot(Fitness ~ Generation, data=AspFitness,
    xlab="Generation", ylab="Fitness", main="ASP Fitness")
dev.off()
png("graphs/AspFitness-boxplot-log.png", width=800, height=800)
boxplot(log(Fitness) ~ Generation, data=AspFitness,
    xlab="Generation", ylab="log(fitness)", main="ASP Fitness")
dev.off()

png("graphs/AspCapitalAssets-boxplot.png", width=800, height=800)
boxplot(CapitalAssets ~ Generation, data=AspFitness,
    xlab="Generation", ylab="Capital Assets", main="ASP Capital Assets")
dev.off()
png("graphs/AspCapitalAssets-boxplot-log.png", width=800, height=800)
boxplot(log(CapitalAssets) ~ Generation, data=AspFitness,
    xlab="Generation", ylab="log(Capital Assets", main="ASP Capital Assets")
dev.off()

# Number of Customers
png("graphs/AspCustomers-boxplot-log.png", width=800, height=800)
boxplot(NumCustomers ~ Generation, data=AspFitness,
    xlab="Generation", ylab="# of Customers", main = "# of ASP Customers")
dev.off()

# Fitness(Investment)
png("graphs/AspFitnessInvestmet-hexbin.png", width=800, height=800)
bin <- hexbin(AspFitness$TotalInvestment, AspFitness$Fitness, xbins=50)
plot(bin, main= "ASP Fitness v. Investment", xlab="Total Investment", ylab="")
dev.off()
rm(bin)

# Fitness(Quality)
png("graphs/AspFitnessQuality-hexbin.png", width=800, height=800)
bin <- hexbin(AspFitness$Quality, AspFitness$Fitness, xbins=50)
plot(bin, main= "ASP Fitness v. Quality", xlab="ASP Quality", ylab="")
dev.off()
rm(bin)

# ASP Fitness(#Customers)
png("graphs/AspFitnessNumCustomers-hexbin.png", width=800, height=800)
bin <- hexbin(AspFitness$NumCustomers, AspFitness$Fitness, xbins=50)
plot(bin, main= "ASP Fitness v. # of Customers", 
xlab="# of Customers", ylab="ASP Fitness")
dev.off()
rm(bin)


png("graphs/AspInvestment-boxplot.png", width=800, height=800)
boxplot(TotalInvestment~ Generation, data=AspFitness,
    xlab="Generation", ylab="Total Investment", main="ASP Investment")
dev.off()
png("graphs/AspInvestment-boxplot-log.png", width=800, height=800)
boxplot(log(TotalInvestment) ~ Generation, data=AspFitness,
    xlab="Generation", ylab="log(Total Investment)", main="ASP Investment")
dev.off()

# ASP Operating
png("graphs/AspQuality-boxplot-log.png", width=800, height=800)
boxplot(Quality ~ Generation, data=AspFitness,
    xlab="Generation", ylab="Quality", main = "ASP Quality")
dev.off()


## NSP/ASP Interconnection Data
# Prices by Generation
png("graphs/InterconnectionPrice-boxplot.png", width=800, height=800)
boxplot(log10(Price) ~ Generation, data=AspInterconnection,
    xlab="Generation", ylab="log10(Price)", main="Interconnection Prices by Generation")
dev.off()

# Quality vs Price
png("graphs/InterconnectionPriceQuantity-hexbin.png", width=800, height=800)
bin <- hexbin(AspInterconnection$Quantity, AspInterconnection$Price, xbins=50)
plot(bin, main= "ASP/NSP Interconnection Market", xlab="Bandwidth Price", ylab="Qty Purchased")
dev.off()
rm(bin)




