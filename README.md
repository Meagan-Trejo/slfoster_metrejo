Robocode Report
---------------

The robocode assignment has been a challenging and fun experience. Initially, we wanted to have our robot do many things, including:
+ Anti-gravity behavior
+ Wall smoothing
+ Wave surfing
+ Tracking
+ Implementing a bounty system

We got a bit too ambitious with what we wanted. We got the anti-gravity to work and created a Bounty class,
but we decided that the movement would be better if we tried a different approach. We decided to start *almost*
completely from scratch. The first draft was to put in the Bounty class and implement tracking behavior. The 
Bounty class instead proved difficult to utilize, so we removed it. Following, we tested our robot against 
some of the sample bots. It fared better than our past attempts, however, we noticed that one bot continually 
beat it and all the others, the sample bot **Walls.** So we decided to pull from its behavior and add it to our
own. We wanted to make the default behavior like that of **Walls**, so that it would stick to the perimeter and
shoot at robots it passed, but if it noticed a bot with a low enough energy level it would track and ram it. 
Once the other robot was dead it would return to staying along the perimeter.

Our robot did fare better after these adjustments, however, our conditions for the tracking behavior is likely
too restrictive, since it rarely tracks the other bots.

We learned a lot from this project, such as strategic analysis, implementation of geometric principles in code,
and not to be too ambitious.

This was a very fun way to learn more about coding and see its application on our screens. Thank you for
introducing us to it.
