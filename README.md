# AspectCache

AspectCache is a Java framework for application-level caching with automatic invalidation.

The caching solutions available today for application-generated objects are difficult to use
as they offer little help for the management of stored entries: the duty of keeping them up to
date is left to application, which is a common cause of errors and makes code maintenance
difficult. 

AspectCache exploits aspect programming techniques and code weaving to cache selected methods and
to keep the stored entries up to date. It does so by establishing dependencies between an
entry and the data accessed during its computation. The framework's simple programming
model requires only minimal modifications to the existing code and no specific
maintenance of the cached methods during the application development. It works without
increasing the algorithmic complexity of the method it operates on and invalidation of an
entry is performed in O(1) time; its memory footprint increases linearly with the number of
recorded dependencies, which is in turn proportional to the input size of the algorithm
represented by the method.
