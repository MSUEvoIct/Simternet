.mode csv

-- ASP Fitness
create table ASPFitness(
Generation integer, 
Chunk integer, 
Step integer, 
ASP string, 
Fitness double, 
Bandwidth double, 
Quality double, 
NumCustomers double, 
CapitalAssets double, 
LiquidAssets double, 
TotalInvestment double, 
TotalFinancing double, 
TotalOperating double, 
TotalRevenue double);
.import ASPFitness.csv ASPFitness
delete from ASPFitness where Generation = 'Generation';

-- Consumer Data
create table AggregateConsumerData(
Generation integer, 
Chunk integer, 
Step integer, 
NumNetworkUsers double,
NumAppSubscriptions integer,
BenefitReceived double,
TransferRequested double,
TransferReceived
);
.import AggregateConsumerData.csv AggregateConsumerData
delete from AggregateConsumerData where Generation = 'Generation';

-- EdgeMarket
create table EdgeMarket(
Generation integer, 
Chunk integer, 
Step integer, 
LocationX integer, 
LocationY integer, 
NumConsumers double, 
NumNetworks integer);
.import EdgeMarket.csv EdgeMarket
delete from EdgeMarket where Generation = 'Generation';

-- NSP Fitness
create table NSPFitness(
Generation integer, 
Chunk integer, 
Step integer, 
NSP string, 
Fitness double, 
CapitalAssets double, 
LiquidAssets double, 
TotalInvestment double, 
TotalFinancing double, 
TotalOperating double, 
TotalRevenue double, 
NumEdges integer, 
Bankrupt boolean, 
NumCustomers double, 
MarketShare double);
.import NSPFitness.csv NSPFitness
delete from NSPFitness where Generation = 'Generation';

-- Backbone Info
create table BackboneInfo(
Generation integer, 
Chunk integer, 
Step integer, 
Source string, 
Destination string, 
PerStepDemand double, 
PerStepTransmitted double, 
TotalDemand double, 
TotalTransmitted double, 
TotalCapacity double);
.import BackboneInfo.csv BackboneInfo
delete from BackboneInfo where Generation = 'Generation';


-- Edge Data
create table EdgeData(
Generation integer,
Chunk integer,
Step integer,
LocationX integer,
LocationY integer,
NSP string,
TransitBandwidth double,
LocalBandwidth double,
Congestion double,
Price double,
Customers double,
Competitors integer,
MarketShare double,
TotalUsage double
);
.import EdgeData.csv EdgeData
delete from EdgeData where Generation = 'Generation';


-- Interconnection
create table NSPASPInterconnection(
Generation integer,
Chunk integer,
Step integer,
NSP string,
ASP string,
Price double,
Quantity double,
Congestion double
);
.import NSP-ASP-Interconnection.csv NSPASPInterconnection
delete from NSPASPInterconnection where Generation = 'Generation';


-- Create Indexes
-- On tablees EdgeData, NSPASPInterconnection, BackboneInfo, NSPFitness, EdgeMarket, ConsumerData, ASPFitness
create unique index idx_ASPFitness_pk on ASPFitness(generation, chunk, step, ASP);
create index idx_ASPFitness_generation on ASPFitness(generation);
create index idx_ASPFitness_chunk on ASPFitness(chunk);
create index idx_ASPFitness_step on ASPFitness(step);



