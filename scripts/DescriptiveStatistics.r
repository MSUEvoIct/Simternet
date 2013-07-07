data <- read.table("Simternet.out.job0.tsv", header=TRUE)

h <- 600
w <- 1200


#### ASP Descriptive Statistics ####

png(filename="BoxPlot.Gen.aspProfit.png", height=h, width=w)
boxplot(data$aspProfit ~ data$Generation, xlab="Generation", ylab="Mean Profit (Fitness)", main = "Profitability of ASPs")
dev.off()

png(filename="BoxPlot.Gen.aspInvestment.png", height=h, width=w)
boxplot(data$aspInvestment ~ data$Generation, xlab="Generation", ylab="Mean Investment", main = "Investment of ASPs")
dev.off()

png(filename="BoxPlot.Gen.aspPrice.png", height=h, width=w)
boxplot(data$aspPrice ~ data$Generation, xlab="Generation", ylab="Mean Price", main = "Mean Prices for ASP Services")
dev.off()


png(filename="BoxPlot.Gen.aspSubscriptions.png", height=h, width=w)
boxplot(data$aspSubscriptions ~ data$Generation, xlab="Generation", ylab="Mean # of Subscriptions", main = "Mean # of Subscriptions to ASP Services")
dev.off()


png(filename="BoxPlot.Gen.aspHHI.png", height=h, width=w)
boxplot(data$aspHHI ~ data$Generation, xlab="Generation", ylab="Market Concentration (HHI)", main = "ASP Market Concentration (HHI)")
dev.off()


#### NSP Descriptive Statistics ####


png(filename="BoxPlot.Gen.nspProfit.png", height=h, width=w)
boxplot(data$nspProfit ~ data$Generation, xlab="Generation", ylab="Mean Profit (Fitness)", main = "Profitability of NSPs")
dev.off()


png(filename="BoxPlot.Gen.nspInvestment.png", height=h, width=w)
boxplot(data$nspInvestment ~ data$Generation, xlab="Generation", ylab="Mean Investment", main = "Investment of NSPs")
dev.off()


png(filename="BoxPlot.Gen.edgePrice.png", height=h, width=w)
boxplot(data$edgePrice ~ data$Generation, xlab="Generation", ylab="Price", main = "Prices of Edge Networks")
dev.off()



png(filename="BoxPlot.Gen.edgeSubscriptions.png", height=h, width=w)
boxplot(data$edgeSubscriptions * 100 ~ data$Generation, xlab="Generation", ylab="% Subscribed", main = "Mean % Subscribed to Edge Networks")
dev.off()


png(filename="BoxPlot.Gen.edgeHHI.png", height=h, width=w)
boxplot(data$edgeHHI ~ data$Generation, xlab="Generation", ylab="Market Concentration (HHI)", main = "Edge Network Market Concentration (HHI)")
dev.off()


#### Consumer Descriptive Statistics ####


png(filename="BoxPlot.Gen.consumerSurplus.png", height=h, width=w)
boxplot(data$consumerSurplus ~ data$Generation, xlab="Generation", ylab="Mean Consumer Surplus", main = "Consumer Surplus")
dev.off()





#### Networking Descriptive Statistics ####

png(filename="BoxPlot.Gen.networkBWSent.png", height=h, width=w)
boxplot(data$FlowBWSent ~ data$Generation, xlab="Generation", ylab="Bandwidth", main = "Flow Bandwidth Sent (Per Flow avg/sim)")
dev.off()

png(filename="BoxPlot.Gen.networkBWReceived.png", height=h, width=w)
boxplot(data$FlowBWReceived ~ data$Generation, xlab="Generation", ylab="Bandwidth", main = "Flow Bandwidth Received (Per Flow avg/sim)")
dev.off()

png(filename="BoxPlot.Gen.networkPerASPCongestion.png", height=h, width=w)
boxplot(data$PerASPAvgCongestion * 100 ~ data$Generation, xlab="Generation", ylab="% Congestion", main = "ASP Congestion (All ASP/NSP/Edge/Step avg/sim)")
dev.off()


png(filename="BoxPlot.Gen.ASP-NSP-Bandwidth-Price.png", height=h, width=w)
boxplot(data$avgBackbonePrice ~ data$Generation, xlab="Generation", ylab="Price/BW/TotalPopulation", main = "ASP->NSP Bandwidth Prices (All ASP/NSP/Step)")
dev.off()


png(filename="BoxPlot.Gen.ASP-NSP-Bandwidth-Qty.png", height=h, width=w)
boxplot(data$avgBackbonePurchaseQty ~ data$Generation, xlab="BWPurchased/TotalPopulation", ylab="Average ASP BW/Person", main = "ASP->NSP Bandwidth Purchas Qty (All ASP/NSP/Step)")
dev.off()


#### Population Statistics ####

popSizes <- read.table("popSizes.job0", header=TRUE)

################################################################
################################################################
################################################################
################################################################

# Plots for Genomes


genome0 <- read.table("genome.job0.spop0.tsv",header=TRUE)
genome1 <- read.table("genome.job0.spop1.tsv",header=TRUE)
genome2 <- read.table("genome.job0.spop2.tsv",header=TRUE)








