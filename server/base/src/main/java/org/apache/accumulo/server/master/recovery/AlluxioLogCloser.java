package org.apache.accumulo.server.master.recovery;

import org.apache.accumulo.core.conf.AccumuloConfiguration;
import org.apache.accumulo.server.fs.VolumeManager;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AlluxioLogCloser implements LogCloser {
    private static final Logger log = LoggerFactory.getLogger(MapRLogCloser.class);

    @Override
    public long close(AccumuloConfiguration conf, VolumeManager fs, Path path) throws IOException {
        log.info("Recovering file " + path.toString() + " by changing permission to readonly");
        alluxio.hadoop.FileSystem ns = (alluxio.hadoop.FileSystem) fs.getVolumeByPath(path).getFileSystem();
        FsPermission roPerm = new FsPermission((short) 0444);
        try {
            ns.setPermission(path, roPerm);
            return 0;
        } catch (IOException ex) {
            log.error("error recovering lease ", ex);
            // lets do this again
            return 1000;
        }
    }

}
