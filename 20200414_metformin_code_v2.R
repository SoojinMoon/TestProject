#################### 20200414 ##########################

########################################################
################### data loading ########################
#########################################################
setwd("~/Downloads")
# finaldat <- read.csv("final_metformin190924.csv") # PI 포함 안 된 데이터 
finaldat <- read.csv("final_metformin200414.csv") # PI 포함 된 데이터 
for(i in c(13:28)) finaldat[,i] <- factor(finaldat[,i], c(0,1), c("0", "1")) # factorize medicine, cancer 
finaldat$SEX_JK <- factor(finaldat$SEX_JK, c("M", "F"))
finaldat$CTRB2 <- factor(finaldat$CTRB2, c("low", "middle", "high"))
finaldat$SMK_STAT_TYPE_RSPS_CD <- factor(finaldat$SMK_STAT_TYPE_RSPS_CD)

# write.csv(finaldat, "final_metformin200414.csv", row.names = F)
#########################################################
################### Table 2 main effect model  ##########
#########################################################
library(survival)
main_obj <- coxph(formula = Surv(duration, Death == 1) ~ SEX_JK + AGE_JK + 
                    BMI_GJ + I(BMI_GJ^2) + BP_LWST_GJ + GAMMA_GTP + CREATININE + 
                    METFORMIN + MEDI_BP + MEDI_BLDS_NOT_METFORMIN +
                    CTRB2 + WLK30_WEK_FREQ_ID2 + 
                    SMK_STAT_TYPE_RSPS_CD + Cancer, data = finaldat)
summary(main_obj)

main_ph = cox.zph(main_obj)
main_ph




#########################################################
##################### Supp table 6  #####################
#########################################################

# other컬럼 - 8대 암이 아닌 암환자
ind <- (finaldat $ stomach == "0" & finaldat $ liver == "0" & finaldat $ colorectal == "0" & finaldat $ breast == "0" & finaldat $ uterine == "0" & finaldat $ lung == "0" & finaldat $ prostate == "0" & finaldat $ thyroid == "0" & finaldat $ Cancer == "1" )
finaldat $ other <- NA; finaldat[ind, 'other'] <- 1; finaldat[!ind, 'other'] <- 0; 

supple.obj <- update(main_obj, .~. - Cancer + METFORMIN*stomach + METFORMIN*thyroid + METFORMIN*breast + METFORMIN*colorectal + METFORMIN*prostate + METFORMIN*lung + METFORMIN*liver + METFORMIN*other, data = finaldat)

supple.tb <- summary(supple.obj)$coefficients[25:32,]
z <- qnorm(0.975)
t.f <- function(x)  data.frame(HR = round(x[,2],3),  Lower = round(exp(x[,1]-z*x[,3]),2), 
                                   Upper = round(exp(x[,1]+z*x[,3]),2), p_value = round(x[,5],4)) 
t.f(supple.tb)


#########################################################
#### Figure 3 Hazard ratio(Cancer:Metformin group)  #####
############ Interaction effect  ########################
#########################################################

###### 20200413 - group1에서 metformin빼면 효과 유의하게 안 나옴
########## group1 vs. group2 로 분석했을 때? 
finaldat$C_grp <- NA

finaldat[finaldat$liver == "1"|finaldat$lung == "1"|finaldat$prostate == "1"|finaldat$colorectal == "1" , 'C_grp'] <- "group1"
finaldat[finaldat$stomach == "1" | finaldat$thyroid == "1" | finaldat$uterine == "1"| finaldat$breast == "1", 'C_grp'] <- "group2"
finaldat[finaldat$Cancer == "1" & is.na(finaldat$C_grp),]$C_grp <- "group3"
finaldat[finaldat$Cancer == "0" & is.na(finaldat$C_grp),]$C_grp <- "No Cancer";
finaldat$C_grp <- factor(finaldat$C_grp, c("No Cancer","group1","group2","group3"))
table(finaldat$C_grp)

chisq.test(table(finaldat$C_grp, finaldat$METFORMIN))

# HR of metformin for C_grp
inter_obj <- update(main_obj, .~. - Cancer + METFORMIN*C_grp, data = finaldat)
summary(inter_obj)

finaldat$C_grp <- factor(finaldat$C_grp, c("No Cancer", "group1", "group2", "group3"))
inter_obj1 <- update(inter_obj, .~., data = finaldat)

finaldat$C_grp <- factor(finaldat$C_grp, c("group1", "No Cancer", "group2", "group3"))
inter_obj2 <- update(inter_obj, .~., data = finaldat)

finaldat$C_grp <- factor(finaldat$C_grp, c("group2", "No Cancer", "group1", "group3"))
inter_obj3 <- update(inter_obj, .~., data = finaldat)

finaldat$C_grp <- factor(finaldat$C_grp, c("group3", "No Cancer", "group1", "group2"))
inter_obj4 <- update(inter_obj, .~., data = finaldat)

NocancerMet <- round(summary(inter_obj1)$coef["METFORMIN1", ], 4) # HR of Metformin in No cancer group
group1Met <- round(summary(inter_obj2)$coef["METFORMIN1", ], 4) # HR of Metformin in No cancer group
group2Met <- round(summary(inter_obj3)$coef["METFORMIN1", ], 4) # HR of Metformin in No cancer group
group3Met <- round(summary(inter_obj4)$coef["METFORMIN1", ], 4) # HR of Metformin in No cancer group

z <- qnorm(0.975)
table.f <- function(x)  data.frame(HR = round(x[2],2),  Lower = round(exp(x[1]-z*x[3]),2), 
                                   Upper = round(exp(x[1]+z*x[3]),2), p_value = round(x[5],4)) 
tb_new1 <- t(sapply(list(NocancerMet, group1Met, group2Met,group3Met), table.f))
rownames(tb_new1) <- c("No cancer", "group1", "group2", "group3")
tb_new1
# table(finaldat$C_grp, finaldat$Death)

draw_fC_grp <- function(dfr = tb_new1, xlimv = c(-0.1, 1.5), seqv =0.5, adjp = c(0.025,-0.05,0.025)){
  plot(NA, ylim=c(0.5,12.5), xlim = xlimv, xaxt = "n", type = "n", yaxt= "n",xlab = "" ,ylab = "", lwd = 2);
  abline(v=seq(0, xlimv[2], seqv), lty = 1, lwd = 0.5, col = "grey95"); 
  abline(v = 1, lty = 2, col = "grey95")
  abline(h=c(11, 8, 5, 2), lty = 1, lwd = 0.5, col = "grey95");
  col <- c("grey50", "red", "blue", "green")
  for(i in c(4,3,2,1)){ 
    # i <- 1
    ind <- which(i == c(4,3,2,1))
    lines(c(dfr[ind,2], dfr[ind,3]), c(i,i)*3-1, lwd = 2, col = col[ind]); 
    lines(c(dfr[ind,2], dfr[ind,2]), c(i-0.05, i+0.05)*3-1, lwd = 2, col = col[ind]);  
    lines(c(dfr[ind,3], dfr[ind,3]), c(i-0.05, i+ 0.05)*3-1, lwd = 2, col = col[ind]); 
    points(dfr[ind,1], i*3-1, pch = 20, col =col[ind]); 
    text(x=dfr[ind,1][[1]] + adjp[1], 12 - ind*3 + 2 - 0.5, paste(round(dfr[ind,1][[1]],2), sep = ""))
    text(x=dfr[ind,2][[1]] + adjp[2], 12 - ind*3 + 2 + 0.5, paste(round(dfr[ind,2][[1]],2), sep = ""))
    text(x=dfr[ind,3][[1]] + adjp[3], 12 - ind*3 + 2 + 0.5, paste(round(dfr[ind,3][[1]],2), sep = ""))
    text(x=dfr[ind,1][[1]] + 0, 12 - ind*3 + 2 - 1, paste("p-value ", pvalues[ind], sep = ""), col = "grey50")
  }
  axis(1, at = c(0,1)); 
  mtext(text = "Hazard Ratios with CIs", 1, line = 3, at = 0.7); 
  mtext(c("Cancer group3 (CG3)", "Cancer group2 (CG2)", "Cancer group1 (CG1)", "No cancer group (No CG)"), 2, line = 1, las = 1, at = c(2,5,8,11), adj =1)
}


postscript("20200414_metformin/Fig3/metformin_effect_tab_ver2.eps", width = 6.5, height = 6.5)
# pdf("metformin_effect_tab.pdf", width = 10, height = 5)
pvalues <- c("<0.001", "<0.001","=0.329","<0.001")
par(mfrow = c(1,1), mar = c(5,12,0,0), pty = "s")
draw_fC_grp(dfr = tb_new1, xlimv = c(-0.1, 1.5), seqv =0.2, adjp = c(0, 0, 0))
dev.off()



#########################################################
############ Figure 2 adjusted survival curve  ##########
############ Metformin vs. Non-metformin ################
#########################################################

newdat.f <- function(x){
  if(is.factor(x)) factor(x = levels(x)[1], levels = levels(x) )
  #else if(length(unique(x))==2) 0
  else mean(x, na.rm = T)
}

newdat <- lapply(subset(finaldat, 
                        select = c(SEX_JK, AGE_JK, BMI_GJ,
                                   BP_LWST_GJ, CREATININE, GAMMA_GTP, 
                                   MEDI_BP, MEDI_BLDS_NOT_METFORMIN, CTRB2,  
                                   WLK30_WEK_FREQ_ID2,
                                   SMK_STAT_TYPE_RSPS_CD, Cancer)), newdat.f)
newdat <- data.frame(newdat)
newdata1 <- rbind(newdat, newdat)
newdata1$METFORMIN <- factor( x = c(0,1), levels = c("0","1"))

surv <- survfit( main_obj, newdata = newdata1)
library(RColorBrewer)
cols = c( brewer.pal(n = 5, name = "Greys")[3], brewer.pal(n = 5, name = "YlOrRd")[2], brewer.pal(n = 5, name = "Blues")[5] )
cols = cols[2:3]

#pdf("metformin_effect_all_patients200412.pdf", width = 5, height = 5)
postscript("20200414_metformin/metformin_effect_all_patients200415.eps", width = 5, height = 5)
par( mar = c(5,5,1,0))
plot(surv, lwd = 2, ylim = c(0.96, 1), 
     ylab = "Survival rate (%)", xlab = "Follow-up duration (years)",
     col = cols, lty = c(2,1), xlim = c(-0.2, 5.2));# , bty = "c"); 
legend( x = 0, y = 0.967, legend = paste(c("Non-metformin", "Metformin")), lwd = 3, lty = c(2,1), col = cols, box.col = "grey80" )
text( x = 2, y = 0.98, paste("p-value <0.001", sep = ""))
dev.off()

#NA 가 다른 변수들에 들어있어서 # at risk 정보는 넣을 수가 없음. Sample size가 변하게 됨
#apply(finaldat, 2, function(x) sum(is.na(x)) )


#########################################################
############ Figure 4 adjusted survival curve  ##########
############ Interaction effect  ########################
#########################################################

newdat.f <- function(x){
  if(is.factor(x)) factor(x = levels(x)[1], levels = levels(x) )
  #else if(length(unique(x))==2) 0
  else mean(x, na.rm = T)
}

newdat <- lapply(subset(finaldat, 
                        select = c(SEX_JK, AGE_JK, BMI_GJ,
                                   BP_LWST_GJ, CREATININE, GAMMA_GTP, 
                                   MEDI_BP, MEDI_BLDS_NOT_METFORMIN, CTRB2,  
                                   WLK30_WEK_FREQ_ID2,
                                   SMK_STAT_TYPE_RSPS_CD, Cancer)), newdat.f)
newdat <- data.frame(newdat)
newdatlst <- expand.grid(METFORMIN = c("0","1"), C_grp = c("No Cancer", "group1", "group2","group3"))

newdata1 <- apply(newdatlst, 1,function(x) data.frame(newdat, METFORMIN = x[1], C_grp = x[2]))
newdata1 <- Reduce(f = rbind, newdata1)

surv <- list()
surv[[1]] <- survfit(inter_obj1, newdata = newdata1[1:2,])
surv[[2]] <- survfit(inter_obj2, newdata = newdata1[3:4,])
surv[[3]] <- survfit(inter_obj3, newdata = newdata1[5:6,])
surv[[4]] <- survfit(inter_obj3, newdata = newdata1[7:8,])

pvalues <- round(sapply(list(inter_obj1,inter_obj2,inter_obj3,inter_obj4), function(x) summary(x)$coefficients["METFORMIN1", 5]),4)
pvalues[pvalues<0.001] <- "<0.001"
pvalues[3] <- "=0.329"

library(RColorBrewer)
cols = c( brewer.pal(n = 5, name = "Greys")[3], brewer.pal(n = 5, name = "YlOrRd")[2], brewer.pal(n = 5, name = "Blues")[5] )
cols = cols[2:3]


postscript("20200414_metformin/metformin_effect_cg123_200415.eps", width = 18, height = 6)
# pdf(paste("metformin_effect_cg12","_200412.pdf", sep=""), width = 10, height = 5)
abc <- c("A", "B", "C")
par(mfrow = c(1,3), mar = c(5,5,1,4), pty = "s")
for(i in 2:4){
  # pdf(paste("20200413_metformin/metformin_effect_cg",i,"_200412.pdf", sep=""), width = 5, height = 5)
  # postscript(paste("20200414_metformin/metformin_effect_cg",i-1,"_200416.eps", sep=""), width = 5, height = 5)
  # par(mfrow = c(1,1),mar = c(4,5,3,0), pty = "s")
  plot(surv[[i]], lwd = 2, # bty= "c",
       ylim = c(0.89, 1), 
       ylab = "Survival rate (%)", xlab = "Follow-up duration (years)",
       col = cols, lty = c(2,1), xlim = c(-0.2, 5.2))
  legend( x = 0.1, y = 0.915, legend = paste(c("Non-metformin", "Metformin")), lwd = 3, lty = c(4,1), col = cols, box.col = "grey80")
  text( x = 1.8, y = 0.94, paste("p-value ",pvalues[i], sep = ""))
  mtext(text = abc[i-1], line = -0.2, las = 1, adj = 1, at = -1.3, cex = 1.3)
  # dev.off()
} 

dev.off()

#########################################################
#######  Supplementary table 1-1 interaction model  #####
#######  Cancer; base group = No cancer group  ##########
#########################################################
t.f <- function(x)  data.frame(HR = round(x[,2],2),  Lower = round(exp(x[,1]-z*x[,3]),3), 
                               Upper = round(exp(x[,1]+z*x[,3]),2), p_value = round(x[,5],3)) 

inter_obj1
t.f(summary(inter_obj1)$coefficients)

#########################################################
#######  Supplementary table 1-2 interaction model  #####
#######  Cancer; base group = Cancer group 1  ###########
#########################################################

inter_obj2
t.f(summary(inter_obj2)$coefficients)

#########################################################
#######  Supplementary table 1-3 interaction model  #####
#######  Cancer; base group = Cancer group 2  ###########
#########################################################

inter_obj3
t.f(summary(inter_obj3)$coefficients)

#########################################################
#######  Supplementary table 1-4 interaction model  #####
#######  Cancer; base group = Cancer group 3  ###########
#########################################################

inter_obj4
t.f(summary(inter_obj4)$coefficients)


#########################################################
#######  Supplementary table 2 interaction model  #######
####  prevalence rate of eight major cancer sites  ######
#########################################################

sapply(finaldat[,c(19:27)], table)[2,]
round(sapply(finaldat[,c(19:27)], table)[2,]/table(finaldat[,"Cancer"])[2] * 100, 2)


#########################################################
#######  Supplementary table 7 ###
#########################################################
other_cancers <- read.csv("~/Downloads/other_cancers_data_20200415.csv")
sick.f <- function(dat){
  ifelse(dat[2] == "NULL", dat[3], dat[2])
}
other_cancers$sick_sym_0910 <- apply(other_cancers, 1, sick.f)
other_cancer_df <- data.frame(table(other_cancers$sick_sym_0910))
other_cancer_df
