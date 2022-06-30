require 'English'
require 'fileutils'
require 'open3'

module Docker
  module Antivirus
    # Helpers Module
    module Helpers
      module_function

      def atomic_mount(image)
        puts "Mounting #{image}"
        stdout, stderr, status = Open3.capture3("podman image mount #{image}")
        puts "Mounting #{image} in #{stdout}"
        stdout
      end

      def clamav_scan(image, directory)
        puts "Scanning #{image} in #{directory} with ClamAV"
        system("clamscan -r -i -z #{directory}")
      end

      def cleanup()
        system("podman image unmount --all")
        puts "Directory cleaned up"
      end
    end
  end
end
