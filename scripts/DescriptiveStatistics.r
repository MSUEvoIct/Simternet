data <- read.table("Simternet.out.job0.tsv", header=TRUE)

h <- 600
w <- 1200

#### ASP Descriptive Statistics ####

png(filename="Plot.Gen.aspProfit.png", height=h, width=w)
plot(data$Generation,data$aspProfit_mean, xlab="Generation", ylab="Mean Profit (Fitness)", main = "Profitability of ASPs")
dev.off()

png(filename="Plot.Gen.aspInvestment.png", height=h, width=w)
plot(data$Generation,data$aspInvestment_mean, xlab="Generation", ylab="Mean Investment", main = "Investment of ASPs")
dev.off()


png(filename="Plot.Gen.aspPrice.png", height=h, width=w)
plot(data$Generation,data$aspPrice_mean, xlab="Generation", ylab="Mean Price", main = "Mean Prices for ASP Services")
dev.off()

png(filename="Plot.Gen.aspSubscriptions.png", height=h, width=w)
plot(data$Generation,data$aspSubscriptions_mean, xlab="Generation", ylab="Mean # of Subscriptions", main = "Mean # of Subscriptions to ASP Services")
dev.off()

png(filename="Plot.Gen.aspGini.png", height=h, width=w)
plot(data$Generation,data$aspGini_mean, xlab="Generation", ylab="Market Concentration (Gini)", main = "ASP Market Concentration (Gini)")
dev.off()



#### NSP Descriptive Statistics ####

png(filename="Plot.Gen.nspProfit.png", height=h, width=w)
plot(data$Generation,data$nspProfit_mean, xlab="Generation", ylab="Mean Profit (Fitness)", main = "Profitability of NSPs")
dev.off()

png(filename="Plot.Gen.nspInvestment.png", height=h, width=w)
plot(data$Generation,data$nspInvestment_mean, xlab="Generation", ylab="Mean Investment", main = "Investment of NSPs")
dev.off()


png(filename="Plot.Gen.edgePrice.png", height=h, width=w)
plot(data$Generation,data$edgePrice_mean, xlab="Generation", ylab="Price", main = "Prices of Edge Networks")
dev.off()

png(filename="Plot.Gen.edgeSubscriptions.png", height=h, width=w)
plot(data$Generation,data$edgeSubscriptions_mean * 100, xlab="Generation", ylab="% Subscribed", main = "Mean % Subscribed to Edge Networks")
dev.off()

png(filename="Plot.Gen.edgeGini.png", height=h, width=w)
plot(data$Generation,data$edgeGini_mean, xlab="Generation", ylab="Market Concentration (Gini)", main = "Edge Network Market Concentration (Gini)")
dev.off()



#### Consumer Descriptive Statistics ####


png(filename="Plot.Gen.consumerSurplus.png", height=h, width=w)
plot(data$Generation,data$consumerSurplus_mean, xlab="Generation", ylab="Mean Consumer Surplus", main = "Consumer Surplus")
dev.off()





######### The Same, but when not aggregated. ##############

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


png(filename="BoxPlot.Gen.aspGini.png", height=h, width=w)
boxplot(data$aspGini ~ data$Generation, xlab="Generation", ylab="Market Concentration (Gini)", main = "ASP Market Concentration (Gini)")
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


png(filename="BoxPlot.Gen.edgeGini.png", height=h, width=w)
boxplot(data$edgeGini ~ data$Generation, xlab="Generation", ylab="Market Concentration (Gini)", main = "Edge Network Market Concentration (Gini)")
dev.off()


#### Consumer Descriptive Statistics ####


png(filename="BoxPlot.Gen.consumerSurplus.png", height=h, width=w)
boxplot(data$consumerSurplus ~ data$Generation, xlab="Generation", ylab="Mean Consumer Surplus", main = "Consumer Surplus")
dev.off()




#### Population Statistics ####

popSizes <- read.table("popSizes.job0", header=TRUE)






################################################################
################################################################
################################################################
################################################################

# Plots for Genomes


genome0 <- read.table("genome.job0.spop0",header=TRUE)
genome1 <- read.table("genome.job0.spop1",header=TRUE)
genome2 <- read.table("genome.job0.spop2",header=TRUE)








