data <- read.table("Simternet.out.job0.tsv", header=TRUE)

h <- 600
w <- 600

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