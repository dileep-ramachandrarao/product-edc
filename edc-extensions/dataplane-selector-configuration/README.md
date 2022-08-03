# Data Plane Selector Configuration Exception

This extension makes it possible configure one or more data planes in the control plane. After a data transfer is
triggered at the control plane, it will look for a data plane with matching capabilities. If no data plane is found, that supports
the correct data source and data sink, the data transfer cannot be done.

## Configuration

| Key                                                       | Description                                                                               | Mandatory | Default          |
|:----------------------------------------------------------|:------------------------------------------------------------------------------------------|-----------|------------------|
| edc.dataplane.selector.<dataplane-id>.url                 | URL of the Data-Plane instance                                                            | X         |                  |
| edc.dataplane.selector.<dataplane-id>.destinationtypes    | Comma separated list of destination transfer types supported of that Data-Plane instance  |           | '' (empty string)|
| edc.dataplane.selector.<dataplane-id>.sourcetypes         | Comma separated list of source transfer types supported of that Data-Plane instance       |           | '' (empty string)|
