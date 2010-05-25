Instructions for Graphing Parametric Analysis
---------------------------------------------

1) Install "R" on your Linux system (sudo yum install R)
2) $ cd analysis/R
3) $ R
4) > install.package("reshape")
   # first time only -- follow prompts, take default archive, pick a CRAN mirror
5) > dev.set(2); pairs(params2[1:50,c(1,6)],panel=smooth,diag.panel=panel.hist)
   # plots the pairwise relationship between video # and the objective (goal) amount

6) > dev.set(3); pairs(params2[1:50,2:5],panel=smooth,diag.panel=panel.hist)
   # plots the pairwise relationships between the alpha, median, momentum_threshold and picture_threshold

7) > pairs(params[1:50,c(1,6)],panel=smooth,diag.panel=panel.hist)
   # plots the v-o.bmp

Interpreting
------------
A good parameter fit consists of alpha/median/momentum_threshold/picture_threshold graphs that are tightly
clustered.  Good parameter fit is indicated by horizontal smoothing lines, which implies that the parameters
are not highly correlated.

There are three example bitmaps in this directory taken from a parametric analysis run of the MPP detector
against four predator videos.  The results were captured in to params.txt and params2.txt by visiting
the following URL which encodes various constraints on the detector outputs:

http://percival.appscio.com:28080/LDE/speedupAnalysis/parameters?show=500&max_percent_motion=80&max_total_mpp_objects=10&

The charts were made using 200 rows of the params & params2 data frames (instead of the 50 recommended above).
This is intended to show the effect of taking "too much data" or "too poor an objective result" into the
parameter estimation mechanism.  Note that the graphs have poor clustering, indicating that any one set of parameters
will provide a compromise performance in the detector.

 o t_p_opt.bmp: the total_mpp_objects/percent_motion ratio objective (params.txt)
 o t_s_opt.bmp: the total_mpp_objects/num_mpp_segments objective (params2.txt)
 o v-o.bmp: the video/objective pairwise plot for t_p_opt.

