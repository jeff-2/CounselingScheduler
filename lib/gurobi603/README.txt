Note:

In order to run the optimizer in linearprogram/ECProgram.java, 
you need to install Gurobi (latest version 6.0.3 as of 4/20/2015).

To do this, go to www.gurobi.com and download the .msi (Windows 32/64 bit)
or .tgz (Linux 32/64 bit) file associated with your system, and follow
the installation instructions in the quick-start guides at:

http://www.gurobi.com/documentation/6.0/

---

In Eclipse:

1) Add lib/gurobi.jar to your build path

2) Adjust your Run Configuration to include the 
following Environment Variable:

PATH  <the /bin directory of your Gurobi installation, e.g. C:\gurobi603\win64\bin>