# Headers every proxied request carries. Kept in one file because a `proxy_set_header` inside a
# location cancels inheritance from the server block: each location has to repeat the full set.
proxy_http_version 1.1;
proxy_set_header Host              $host;
proxy_set_header X-Real-IP         $remote_addr;
proxy_set_header X-Forwarded-For   $proxy_add_x_forwarded_for;
proxy_set_header X-Forwarded-Proto $scheme;
proxy_set_header X-Forwarded-Host  $host;
