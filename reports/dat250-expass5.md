# DAT250 Experiment 5

In this assignment, I've played with Valkey, finally using it to implement a way of caching the all polls endpoint
in my polling application.

Since my polling application repository works on the principle of objects with shared references 
(and possible backreferences / recursion etc.), deserializing JSON into the object model is a particularly
difficult task.

Because of this limitation, I implemented the caching on the level of JSON strings.
Whenever the endpoint is requested and cache is present in Valkey, the cached string is returned.
If cache is not present, the string is evaluated from the object repository normally, serialized, and stored in cache.
Any action that would change the result of the get all polls operation deletes the cache item.

This is not the cleanest possible solution, but then, the repository structure itself that this is building on
is not the cleanest possible solution either.

I experienced no technical problems installing Valkey on my computer.
I had some minor issues with making Java willing to serialize my objects, but it was fairly quickly solved with some
googling.
